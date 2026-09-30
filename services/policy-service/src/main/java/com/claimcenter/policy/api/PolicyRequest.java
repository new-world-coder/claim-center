package com.claimcenter.policy.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PolicyRequest(
        @NotBlank String policyNumber,
        @NotNull UUID customerId,
        @NotBlank String status,
        @NotNull @DecimalMin("0.01") BigDecimal coverageAmount
) {
}
