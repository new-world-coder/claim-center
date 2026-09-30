package com.claimcenter.audit.api;

import com.claimcenter.audit.service.AuditApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditApplicationService service;

    public AuditController(AuditApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditResponse> list() {
        return service.list();
    }

    @PostMapping
    public AuditResponse create(@Valid @RequestBody AuditRequest request) {
        return service.create(request);
    }
}
