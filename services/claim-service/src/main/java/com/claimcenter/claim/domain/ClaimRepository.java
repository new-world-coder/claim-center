package com.claimcenter.claim.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {
    long countByPolicyIdAndCreatedAtAfter(UUID policyId, Instant createdAt);
}
