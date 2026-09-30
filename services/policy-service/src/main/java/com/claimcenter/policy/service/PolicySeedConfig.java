package com.claimcenter.policy.service;

import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.policy.domain.PolicyEntity;
import com.claimcenter.policy.domain.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class PolicySeedConfig {
    @Bean
    CommandLineRunner seedPolicies(PolicyRepository policyRepository) {
        return args -> {
            if (policyRepository.count() > 0) {
                return;
            }
            TenantContext.set("acme", "SMALL");
            try {
                PolicyEntity policy = new PolicyEntity();
                policy.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
                policy.setTenantId("acme");
                policy.setPolicyNumber("POL-1001");
                policy.setCustomerId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
                policy.setStatus("ACTIVE");
                policy.setCoverageAmount(new BigDecimal("25000.00"));
                policy.setCreatedAt(Instant.now());
                policyRepository.save(policy);
            } finally {
                TenantContext.clear();
            }
        };
    }
}
