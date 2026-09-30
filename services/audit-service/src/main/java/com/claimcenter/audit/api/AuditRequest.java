package com.claimcenter.audit.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AuditRequest(@NotBlank String action, @NotBlank String resourceName, String details) {
}
