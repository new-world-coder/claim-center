package com.claimcenter.customer.service;

import com.claimcenter.common.security.BearerTokens;
import com.claimcenter.common.tenant.TenantContext;
import com.claimcenter.customer.api.CustomerRequest;
import com.claimcenter.customer.api.CustomerResponse;
import com.claimcenter.customer.domain.CustomerEntity;
import com.claimcenter.customer.domain.CustomerRepository;
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
public class CustomerApplicationService {
    private final CustomerRepository repository;

    public CustomerApplicationService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<CustomerResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CustomerResponse create(CustomerRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setTenantId(TenantContext.get());
        entity.setFullName(request.fullName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setCreatedAt(Instant.now());
        
        return toResponse(repository.save(entity));
    }

    private CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(entity.getId(), entity.getFullName(), entity.getEmail(), entity.getPhone());
    }
}

@Configuration
class CustomerSeedConfig {

    @Bean
    CommandLineRunner seed(CustomerRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            TenantContext.set("acme", "SMALL");
            try {
                CustomerEntity entity = new CustomerEntity();
                entity.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
                entity.setTenantId("acme");
                entity.setFullName("Avery Chen");
                entity.setEmail("avery@acme.test");
                entity.setPhone("555-0142");
                entity.setCreatedAt(Instant.now());
                repository.save(entity);
            } finally { TenantContext.clear(); }
        };
    }

}
