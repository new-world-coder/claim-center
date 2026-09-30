package com.claimcenter.document.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DocumentRequest(@NotBlank String fileName, @NotBlank String contentType, UUID claimId) {
}
