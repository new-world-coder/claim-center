package com.claimcenter.claim.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        String claimNumber,
        UUID policyId,
        UUID customerId,
        BigDecimal amount,
        String description,
        String status,
        boolean fraudFlag
) {
}
