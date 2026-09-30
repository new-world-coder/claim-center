package com.claimcenter.tenant.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OnboardTenantRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "SMALL|LARGE") String tier,
        @NotBlank String adminUsername,
        @NotBlank String adminPassword
) {
}
