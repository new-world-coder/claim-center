package com.claimcenter.tenant.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
    boolean existsByTenantId(String tenantId);
    Optional<TenantEntity> findByTenantId(String tenantId);
}
