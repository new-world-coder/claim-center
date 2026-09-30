package com.claimcenter.policy.api;

import java.math.BigDecimal;
import java.util.UUID;

public record PolicyResponse(UUID id, String policyNumber, UUID customerId, String status, BigDecimal coverageAmount) {
}
