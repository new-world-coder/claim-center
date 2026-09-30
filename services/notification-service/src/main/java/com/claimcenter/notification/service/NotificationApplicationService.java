package com.claimcenter.notification.service;

import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.notification.api.NotificationRequest;
import com.claimcenter.notification.api.NotificationResponse;
import com.claimcenter.notification.domain.NotificationEntity;
import com.claimcenter.notification.domain.NotificationRepository;
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
public class NotificationApplicationService {
    private final NotificationRepository repository;

    public NotificationApplicationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<NotificationResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public NotificationResponse create(NotificationRequest request) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setChannel(request.channel());
        entity.setRecipient(request.recipient());
        entity.setMessage(request.message());
        entity.setStatus("SENT");
        entity.setCreatedAt(Instant.now());
        
        return toResponse(repository.save(entity));
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(entity.getId(), entity.getChannel(), entity.getRecipient(), entity.getMessage(), entity.getStatus());
    }
}

@Configuration
class NotificationSeedConfig {
    @Bean CommandLineRunner noop() { return args -> { }; }
}
