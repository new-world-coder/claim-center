package com.claimcenter.tenant.service;

import com.claimcenter.tenant.domain.TenantEntity;
import com.claimcenter.tenant.domain.TenantRepository;
import com.claimcenter.tenant.domain.TenantSettingsEntity;
import com.claimcenter.tenant.domain.TenantSettingsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class TenantInfraConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner seedTenants(TenantRepository tenantRepository, TenantSettingsRepository settingsRepository,
                                  DedicatedDatabaseProvisioner provisioner) {
        return args -> {
            if (tenantRepository.count() > 0) {
                return;
            }
            save(tenantRepository, settingsRepository, "platform", "Platform", "SMALL", null);
            save(tenantRepository, settingsRepository, "acme", "Acme Mutual", "SMALL", null);
            provisioner.provision("tenant_bigco");
            provisioner.seedSample("tenant_bigco", "bigco");
            save(tenantRepository, settingsRepository, "bigco", "Big Co Insurance", "LARGE", "tenant_bigco");
        };
    }

    private void save(TenantRepository tenantRepository, TenantSettingsRepository settingsRepository, String tenantId,
                      String name, String tier, String dbName) {
        TenantEntity tenant = new TenantEntity();
        tenant.setId(UUID.randomUUID());
        tenant.setTenantId(tenantId);
        tenant.setName(name);
        tenant.setTier(tier);
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
    }
}
