package com.claimcenter.auth.service;

import com.claimcenter.auth.domain.UserEntity;
import com.claimcenter.auth.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class AuthSeedConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            save(userRepository, passwordEncoder, "platform", "SMALL", "platform-admin", "PLATFORM_ADMIN");
            save(userRepository, passwordEncoder, "acme", "SMALL", "acme-admin", "TENANT_ADMIN");
            save(userRepository, passwordEncoder, "acme", "SMALL", "acme-adjuster", "ADJUSTER");
            save(userRepository, passwordEncoder, "acme", "SMALL", "acme-customer", "CUSTOMER");
            save(userRepository, passwordEncoder, "bigco", "LARGE", "bigco-admin", "TENANT_ADMIN");
        };
    }

    private void save(UserRepository userRepository, PasswordEncoder passwordEncoder, String tenantId, String tier,
                      String username, String role) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setTenantId(tenantId);
        user.setTenantTier(tier);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
    }
}
