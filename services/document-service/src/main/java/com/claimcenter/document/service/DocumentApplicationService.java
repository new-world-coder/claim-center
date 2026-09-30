package com.claimcenter.document.service;

import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.document.api.DocumentRequest;
import com.claimcenter.document.api.DocumentResponse;
import com.claimcenter.document.domain.DocumentEntity;
import com.claimcenter.document.domain.DocumentRepository;
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
public class DocumentApplicationService {
    private final DocumentRepository repository;

    public DocumentApplicationService(DocumentRepository repository) {
        this.repository = repository;
    }

    public List<DocumentResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public DocumentResponse create(DocumentRequest request) {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setFileName(request.fileName());
        entity.setContentType(request.contentType());
        entity.setClaimId(request.claimId());
        entity.setCreatedAt(Instant.now());
        entity.setStorageKey(entity.getId().toString());
        return toResponse(repository.save(entity));
    }

    private DocumentResponse toResponse(DocumentEntity entity) {
        return new DocumentResponse(entity.getId(), entity.getFileName(), entity.getContentType(), entity.getClaimId());
    }
}

@Configuration
class DocumentSeedConfig {
    @Bean CommandLineRunner noop() { return args -> { }; }
}
