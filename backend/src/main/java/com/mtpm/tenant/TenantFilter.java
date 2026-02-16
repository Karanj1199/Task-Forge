package com.mtpm.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    private static final AntPathMatcher matcher = new AntPathMatcher();

    private final List<String> excluded = List.of(
            "/auth/**",
            "/error",
            "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean skip = excluded.stream().anyMatch(p -> matcher.match(p, path));

        if (skip) {
            filterChain.doFilter(request, response);
            return;
        }

        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Missing required header: " + TENANT_HEADER);
            return;
        }

        try {
            TenantContext.setTenantId(tenantId.trim());
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
