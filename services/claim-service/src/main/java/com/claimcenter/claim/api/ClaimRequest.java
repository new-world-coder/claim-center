package com.claimcenter.claim.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ClaimRequest(
        @NotNull UUID policyId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank String description
) {
}
