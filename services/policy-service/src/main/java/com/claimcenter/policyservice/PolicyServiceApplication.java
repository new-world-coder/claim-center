package com.claimcenter.policyservice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class PolicyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PolicyServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/policies")
class PolicyServiceController {
    private final PolicyServiceService service;

    PolicyServiceController(PolicyServiceService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody @Valid UpsertRequest request, @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(service.create(request, tenantId));
    }
}

@Service
class PolicyServiceService {
    private final PolicyServiceRepository repository;

    PolicyServiceService(PolicyServiceRepository repository) { this.repository = repository; }

    Map<String, String> create(UpsertRequest request, String tenantId) {
        repository.ping();
        return Map.of("status", "created", "tenant_id", tenantId, "name", request.name());
    }
}

interface PolicyServiceRepository {
    default void ping() {}
}

record UpsertRequest(@NotBlank String name) {}
