package com.claimcenter.auth.service;

import com.claimcenter.auth.api.CreateUserRequest;
import com.claimcenter.auth.api.LoginRequest;
import com.claimcenter.auth.domain.UserEntity;
import com.claimcenter.auth.domain.UserRepository;
import com.claimcenter.common.tenant.TenantContext;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthApplicationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String jwtSecret;

    public AuthApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                  @Value("${claimcenter.jwt.secret}") String jwtSecret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
    }

    public Map<String, Object> login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .filter(UserEntity::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = issue(user);
        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600,
                "tenant_id", user.getTenantId(),
                "tier", user.getTenantTier(),
                "role", user.getRole(),
                "username", user.getUsername()
        );
    }

    public Map<String, String> createUser(CreateUserRequest request) {
        String actorTenant = TenantContext.get();
        boolean platform = isPlatformAdmin();
        if (!platform && (actorTenant == null || !actorTenant.equals(request.tenantId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create users for another tenant");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setTenantId(request.tenantId());
        user.setTenantTier(request.tenantTier());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
        return Map.of("username", user.getUsername(), "tenant_id", user.getTenantId());
    }

    private boolean isPlatformAdmin() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_PLATFORM_ADMIN".equals(authority.getAuthority()));
    }

    private String issue(UserEntity user) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("claim-center")
                    .claim("tenant_id", user.getTenantId())
                    .claim("tier", user.getTenantTier())
                    .claim("roles", List.of(user.getRole()))
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(jwtSecret.getBytes(StandardCharsets.UTF_8)));
            return jwt.serialize();
        } catch (JOSEException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not issue token");
        }
    }
}
