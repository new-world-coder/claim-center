package com.claimcenter.tenant.api;

import com.claimcenter.tenant.service.TenantApplicationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    private final TenantApplicationService tenantApplicationService;

    public TenantController(TenantApplicationService tenantApplicationService) {
        this.tenantApplicationService = tenantApplicationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public List<TenantResponse> list() {
        return tenantApplicationService.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public TenantResponse onboard(@Valid @RequestBody OnboardTenantRequest request) {
        return tenantApplicationService.onboard(request);
    }
}
