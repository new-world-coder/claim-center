package com.claimcenter.policy.service;

import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.policy.api.PolicyRequest;
import com.claimcenter.policy.api.PolicyResponse;
import com.claimcenter.policy.domain.PolicyEntity;
import com.claimcenter.policy.domain.PolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PolicyApplicationService {
    private final PolicyRepository policyRepository;

    public PolicyApplicationService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<PolicyResponse> list() {
        return policyRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PolicyResponse get(UUID id) {
        return policyRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
    }

    public PolicyResponse create(PolicyRequest request) {
        PolicyEntity entity = new PolicyEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setPolicyNumber(request.policyNumber());
        entity.setCustomerId(request.customerId());
        entity.setStatus(request.status());
        entity.setCoverageAmount(request.coverageAmount());
        entity.setCreatedAt(Instant.now());
        return toResponse(policyRepository.save(entity));
    }

    private PolicyResponse toResponse(PolicyEntity entity) {
        return new PolicyResponse(entity.getId(), entity.getPolicyNumber(), entity.getCustomerId(),
                entity.getStatus(), entity.getCoverageAmount());
    }
}
