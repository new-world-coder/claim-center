package com.claimcenter.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthLoginTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void issuesJwtForSeededAdmin() {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/token",
                Map.of("username", "acme-admin", "password", "password"),
                Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("acme", response.getBody().get("tenant_id"));
        assertNotNull(response.getBody().get("access_token"));
    }
}
