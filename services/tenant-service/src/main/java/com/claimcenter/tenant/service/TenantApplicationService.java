package com.claimcenter.tenant.service;

import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.tenant.api.OnboardTenantRequest;
import com.claimcenter.tenant.api.TenantResponse;
import com.claimcenter.tenant.domain.TenantEntity;
import com.claimcenter.tenant.domain.TenantRepository;
import com.claimcenter.tenant.domain.TenantSettingsEntity;
import com.claimcenter.tenant.domain.TenantSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TenantApplicationService {
    private final TenantRepository tenantRepository;
    private final TenantSettingsRepository settingsRepository;
    private final DedicatedDatabaseProvisioner provisioner;
    private final RestClient restClient;
    private final String authUrl;

    public TenantApplicationService(TenantRepository tenantRepository, TenantSettingsRepository settingsRepository,
                                    DedicatedDatabaseProvisioner provisioner, RestClient restClient,
                                    @Value("${claimcenter.auth-url}") String authUrl) {
        this.tenantRepository = tenantRepository;
        this.settingsRepository = settingsRepository;
        this.provisioner = provisioner;
        this.restClient = restClient;
        this.authUrl = authUrl;
    }

    public List<TenantResponse> list() {
        return tenantRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public TenantResponse onboard(OnboardTenantRequest request) {
        String tenantId = slug(request.name());
        if (tenantRepository.existsByTenantId(tenantId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant already exists");
        }
        String dbName = null;
        if ("LARGE".equals(request.tier())) {
            dbName = "tenant_" + tenantId;
            provisioner.provision(dbName);
        }
        TenantEntity tenant = new TenantEntity();
        tenant.setId(UUID.randomUUID());
        tenant.setTenantId(tenantId);
        tenant.setName(request.name());
        tenant.setTier(request.tier());
        tenant.setDbName(dbName);
        tenant.setStatus("ACTIVE");
        tenant.setCreatedAt(Instant.now());
        tenantRepository.save(tenant);

        TenantSettingsEntity settings = new TenantSettingsEntity();
        settings.setId(UUID.randomUUID());
        settings.setTenantId(tenantId);
        settings.setCurrency("USD");
        settings.setTimezone("UTC");
        settings.setClaimAutoApproveLimit(new BigDecimal("5000.00"));
        settingsRepository.save(settings);

        restClient.post()
                .uri(authUrl + "/api/auth/users")
                .header("Authorization", BearerTokens.current())
                .body(Map.of(
                        "tenantId", tenantId,
                        "tenantTier", request.tier(),
                        "username", request.adminUsername(),
                        "password", request.adminPassword(),
                        "role", "TENANT_ADMIN"))
                .retrieve()
                .toBodilessEntity();
        return toResponse(tenant);
    }

    private TenantResponse toResponse(TenantEntity tenant) {
        return new TenantResponse(tenant.getTenantId(), tenant.getName(), tenant.getTier(), tenant.getDbName(), tenant.getStatus());
    }

    static String slug(String name) {
        String slug = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant name is invalid");
        }
        return slug.length() > 40 ? slug.substring(0, 40) : slug;
    }
}
