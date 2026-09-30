package com.claimcenter.notification.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record NotificationRequest(@NotBlank String channel, @NotBlank String recipient, @NotBlank String message) {
}
