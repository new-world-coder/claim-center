package com.claimcenter.tenant.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantSettingsRepository extends JpaRepository<TenantSettingsEntity, UUID> {
    Optional<TenantSettingsEntity> findByTenantId(String tenantId);
}
