package com.claimcenter.common.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

public final class BearerTokens {
    private BearerTokens() {
    }

    public static String current() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token) {
            return "Bearer " + token.getToken().getTokenValue();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
    }

    public static String username() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token) {
            return token.getToken().getSubject();
        }
        return "system";
    }
}
