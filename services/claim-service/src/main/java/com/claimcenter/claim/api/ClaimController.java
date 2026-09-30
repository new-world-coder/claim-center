package com.claimcenter.claim.api;

import com.claimcenter.claim.service.ClaimApplicationService;
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
@RequestMapping("/api/claims")
public class ClaimController {
    private final ClaimApplicationService claimApplicationService;

    public ClaimController(ClaimApplicationService claimApplicationService) {
        this.claimApplicationService = claimApplicationService;
    }

    @GetMapping
    public List<ClaimResponse> list() {
        return claimApplicationService.list();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','TENANT_ADMIN','ADJUSTER')")
    public ClaimResponse submit(@Valid @RequestBody ClaimRequest request) {
        return claimApplicationService.submit(request);
    }

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasAnyRole('ADJUSTER','TENANT_ADMIN')")
    public ClaimResponse decide(@PathVariable UUID id, @Valid @RequestBody DecisionRequest request) {
        return claimApplicationService.decide(id, request);
    }
}
