package com.claimcenter.claim.service;

import java.math.BigDecimal;
import java.util.UUID;

public record PolicyView(UUID id, String policyNumber, UUID customerId, String status, BigDecimal coverageAmount) {
}
