package com.claimcenter.audit.service;

import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.audit.api.AuditRequest;
import com.claimcenter.audit.api.AuditResponse;
import com.claimcenter.audit.domain.AuditLogEntity;
import com.claimcenter.audit.domain.AuditRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditApplicationService {
    private final AuditRepository repository;

    public AuditApplicationService(AuditRepository repository) {
        this.repository = repository;
    }

    public List<AuditResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public AuditResponse create(AuditRequest request) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setActor(BearerTokens.username());
        entity.setAction(request.action());
        entity.setResourceName(request.resourceName());
        entity.setDetails(request.details());
        entity.setCreatedAt(Instant.now());
        
        return toResponse(repository.save(entity));
    }

    private AuditResponse toResponse(AuditLogEntity entity) {
        return new AuditResponse(entity.getId(), entity.getAction(), entity.getResourceName(), entity.getDetails());
    }
}

@Configuration
class AuditSeedConfig {
    @Bean CommandLineRunner noop() { return args -> { }; }
}
