package com.claimcenter.common.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@ConditionalOnProperty(name = "claimcenter.security.mode", havingValue = "issuer")
public class IssuerSecurityConfig {
    @Bean
    SecurityFilterChain issuerSecurityFilterChain(HttpSecurity http, JwtTenantFilter jwtTenantFilter,
                                                  JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        // Bearer tokens are sent on each request. There is no session cookie for a cross-site request to replay.
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/token", "/actuator/health", "/actuator/prometheus", "/actuator/metrics",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .addFilterAfter(jwtTenantFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }
}
