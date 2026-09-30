package com.claimcenter.claim.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DecisionRequest(@NotBlank @Pattern(regexp = "APPROVED|REJECTED") String decision) {
}
