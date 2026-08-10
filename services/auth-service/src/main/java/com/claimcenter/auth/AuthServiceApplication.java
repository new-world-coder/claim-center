package com.claimcenter.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/auth")
class AuthController {
    private final AuthApplicationService authApplicationService;

    AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> issueToken(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authApplicationService.issueToken(request));
    }
}

@Service
class AuthApplicationService {
    private final UserRepository userRepository;

    AuthApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Map<String, String> issueToken(LoginRequest request) {
        userRepository.ping();
        return Map.of("access_token", "demo-jwt-with-tenant-id", "token_type", "Bearer");
    }
}

interface UserRepository {
    default void ping() {}
}

record LoginRequest(@NotBlank String username, @NotBlank String password) {}

