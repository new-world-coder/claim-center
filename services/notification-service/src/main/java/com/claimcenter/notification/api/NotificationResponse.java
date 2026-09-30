package com.claimcenter.notification.api;

import java.util.UUID;

public record NotificationResponse(UUID id, String channel, String recipient, String message, String status) {
}
