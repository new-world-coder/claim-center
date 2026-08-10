package com.claimcenter.auditservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class AuditServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/audit")
class AuditServiceController {
    private final AuditServiceService service;

    AuditServiceController(AuditServiceService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody @Valid UpsertRequest request, @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(service.create(request, tenantId));
    }
}

@Service
class AuditServiceService {
    private final AuditServiceRepository repository;

    AuditServiceService(AuditServiceRepository repository) { this.repository = repository; }

    Map<String, String> create(UpsertRequest request, String tenantId) {
        repository.ping();
        return Map.of("status", "created", "tenant_id", tenantId, "name", request.name());
    }
}

interface AuditServiceRepository {
    default void ping() {}
}

record UpsertRequest(@NotBlank String name) {}
