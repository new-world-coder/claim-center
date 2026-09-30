package com.claimcenter.auth.api;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String tenantId,
        @NotBlank String tenantTier,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role
) {
}
