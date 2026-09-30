package com.claimcenter.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerRequest(@NotBlank String fullName, @NotBlank @Email String email, String phone) {
}
