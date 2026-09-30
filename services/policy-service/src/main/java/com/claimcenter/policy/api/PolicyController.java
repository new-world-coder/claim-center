package com.claimcenter.policy.api;

import com.claimcenter.policy.service.PolicyApplicationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyApplicationService policyApplicationService;

    public PolicyController(PolicyApplicationService policyApplicationService) {
        this.policyApplicationService = policyApplicationService;
    }

    @GetMapping
    public List<PolicyResponse> list() {
        return policyApplicationService.list();
    }

    @GetMapping("/{id}")
    public PolicyResponse get(@PathVariable UUID id) {
        return policyApplicationService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','ADJUSTER')")
    public PolicyResponse create(@Valid @RequestBody PolicyRequest request) {
        return policyApplicationService.create(request);
    }
}
