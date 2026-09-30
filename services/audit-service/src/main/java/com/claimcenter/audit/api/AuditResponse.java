package com.claimcenter.audit.api;

import java.util.UUID;

public record AuditResponse(UUID id, String action, String resourceName, String details) {
}
