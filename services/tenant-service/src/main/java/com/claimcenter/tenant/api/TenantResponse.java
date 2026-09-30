package com.claimcenter.tenant.api;

public record TenantResponse(String tenantId, String name, String tier, String dbName, String status) {
}
