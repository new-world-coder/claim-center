package com.claimcenter.policy.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyRepository extends JpaRepository<PolicyEntity, UUID> {
}
