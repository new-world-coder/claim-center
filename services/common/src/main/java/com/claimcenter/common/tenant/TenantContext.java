package com.claimcenter.common.tenant;

public final class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> TIER = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(String tenantId, String tier) {
        TENANT.set(tenantId);
        TIER.set(tier);
    }

    public static String get() {
        return TENANT.get();
    }

    public static String tier() {
        return TIER.get();
    }

    public static void clear() {
        TENANT.remove();
        TIER.remove();
    }
}
