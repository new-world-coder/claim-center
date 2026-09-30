package com.claimcenter.claim.service;

import java.math.BigDecimal;
import java.time.Instant;

public record FraudCase(
        BigDecimal amount,
        BigDecimal coverageAmount,
        String description,
        int recentClaimsOnPolicy,
        Instant submittedAt
) {
}
