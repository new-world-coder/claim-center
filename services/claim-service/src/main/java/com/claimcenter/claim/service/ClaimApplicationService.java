package com.claimcenter.claim.service;

import com.claimcenter.claim.api.ClaimRequest;
import com.claimcenter.claim.api.ClaimResponse;
import com.claimcenter.claim.api.DecisionRequest;
import com.claimcenter.claim.domain.ClaimEntity;
import com.claimcenter.claim.domain.ClaimRepository;
import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.common.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ClaimApplicationService {
    private static final Logger LOG = LoggerFactory.getLogger(ClaimApplicationService.class);
    private final ClaimRepository claimRepository;
    private final FraudDetector fraudDetector;
    private final RestClient restClient;
    private final String policyUrl;
    private final String notificationUrl;
    private final String auditUrl;

    public ClaimApplicationService(ClaimRepository claimRepository, FraudDetector fraudDetector, RestClient restClient,
                                   @Value("${claimcenter.policy-url}") String policyUrl,
                                   @Value("${claimcenter.notification-url}") String notificationUrl,
                                   @Value("${claimcenter.audit-url}") String auditUrl) {
        this.claimRepository = claimRepository;
        this.fraudDetector = fraudDetector;
        this.restClient = restClient;
        this.policyUrl = policyUrl;
        this.notificationUrl = notificationUrl;
        this.auditUrl = auditUrl;
    }

    public List<ClaimResponse> list() {
        return claimRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ClaimResponse submit(ClaimRequest request) {
        PolicyView policy = loadPolicy(request.policyId());
        if (!"ACTIVE".equals(policy.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Policy is not active");
        }
        if (request.amount().compareTo(policy.coverageAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount exceeds policy coverage");
        }
        Instant submittedAt = Instant.now();
        int recentClaims = (int) claimRepository.countByPolicyIdAndCreatedAtAfter(
                policy.id(), submittedAt.minus(Duration.ofDays(30)));
        FraudScore fraudScore = fraudDetector.score(new FraudCase(
                request.amount(),
                policy.coverageAmount(),
                request.description(),
                recentClaims,
                submittedAt));
        ClaimEntity entity = new ClaimEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setClaimNumber("CLM-" + entity.getId().toString().substring(0, 8).toUpperCase());
        entity.setPolicyId(policy.id());
        entity.setCustomerId(policy.customerId());
        entity.setAmount(request.amount());
        entity.setDescription(request.description());
        entity.setStatus(fraudScore.status());
        entity.setFraudFlag(fraudScore.flagged());
        entity.setFraudScore(fraudScore.score());
        entity.setFraudBand(fraudScore.band());
        entity.setFraudReasons(String.join("; ", fraudScore.reasons()));
        entity.setCreatedAt(submittedAt);
        ClaimEntity saved = claimRepository.save(entity);
        notify(saved);
        audit("CLAIM_SUBMITTED", saved);
        return toResponse(saved);
    }

    public ClaimResponse decide(UUID id, DecisionRequest request) {
        ClaimEntity entity = claimRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        entity.setStatus(request.decision());
        ClaimEntity saved = claimRepository.save(entity);
        notify(saved);
        audit("CLAIM_" + request.decision(), saved);
        return toResponse(saved);
    }

    private PolicyView loadPolicy(UUID policyId) {
        try {
            return restClient.get()
                    .uri(policyUrl + "/api/policies/" + policyId)
                    .header("Authorization", BearerTokens.current())
                    .retrieve()
                    .body(PolicyView.class);
        } catch (RestClientResponseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Policy validation failed");
        }
    }

    private void notify(ClaimEntity claim) {
        try {
            restClient.post()
                    .uri(notificationUrl + "/api/notifications")
                    .header("Authorization", BearerTokens.current())
                    .body(Map.of(
                            "channel", "EMAIL",
                            "recipient", claim.getTenantId() + "-ops@claimcenter.local",
                            "message", claim.getClaimNumber() + " is " + claim.getStatus()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException exception) {
            LOG.warn("Notification failed for {}", claim.getClaimNumber());
        }
    }

    private void audit(String action, ClaimEntity claim) {
        try {
            restClient.post()
                    .uri(auditUrl + "/api/audit")
                    .header("Authorization", BearerTokens.current())
                    .body(Map.of(
                            "action", action,
                            "resourceName", claim.getClaimNumber(),
                            "details", claim.getStatus() + " score " + claim.getFraudScore()
                                    + " " + claim.getFraudBand() + " amount " + claim.getAmount()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException exception) {
            LOG.warn("Audit failed for {}", claim.getClaimNumber());
        }
    }

    private List<String> reasons(String stored) {
        if (stored == null || stored.isBlank()) {
            return List.of();
        }
        return Arrays.stream(stored.split("; ")).filter(reason -> !reason.isBlank()).toList();
    }

    private ClaimResponse toResponse(ClaimEntity entity) {
        return new ClaimResponse(entity.getId(), entity.getClaimNumber(), entity.getPolicyId(), entity.getCustomerId(),
                entity.getAmount(), entity.getDescription(), entity.getStatus(), entity.isFraudFlag(),
                entity.getFraudScore(), entity.getFraudBand(), reasons(entity.getFraudReasons()));
    }
}
