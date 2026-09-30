package com.claimcenter.tenant.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenantSlugTest {
    @Test
    void slugifiesCompanyName() {
        assertEquals("northwind-mutual", TenantApplicationService.slug("Northwind Mutual"));
    }
}
