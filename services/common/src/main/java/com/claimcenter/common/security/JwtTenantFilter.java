package com.claimcenter.common.security;

import com.claimcenter.common.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token) {
                Jwt jwt = token.getToken();
                String tenantId = jwt.getClaimAsString("tenant_id");
                String tier = jwt.getClaimAsString("tier");
                String headerTenant = request.getHeader("X-Tenant-Id");
                if (headerTenant != null && tenantId != null && !headerTenant.equals(tenantId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Tenant header does not match token");
                    return;
                }
                TenantContext.set(tenantId, tier == null ? "SMALL" : tier);
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
