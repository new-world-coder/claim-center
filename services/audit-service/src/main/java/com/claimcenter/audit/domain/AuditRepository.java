package com.claimcenter.audit.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditRepository extends JpaRepository<AuditLogEntity, UUID> {
}
