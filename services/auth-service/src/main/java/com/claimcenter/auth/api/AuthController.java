package com.claimcenter.auth.api;

import com.claimcenter.auth.service.AuthApplicationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/token")
    public Map<String, Object> token(@Valid @RequestBody LoginRequest request) {
        return authApplicationService.login(request);
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return authApplicationService.logout();
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN','TENANT_ADMIN')")
    public Map<String, String> createUser(@Valid @RequestBody CreateUserRequest request) {
        return authApplicationService.createUser(request);
    }
}
