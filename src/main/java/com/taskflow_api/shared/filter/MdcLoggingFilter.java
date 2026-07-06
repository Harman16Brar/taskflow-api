package com.taskflow_api.shared.filter;

import com.taskflow_api.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class MdcLoggingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Generate unique request ID
            String requestId = UUID.randomUUID().toString().substring(0, 8);

            // Extract user ID from JWT if present
            String userId = extractUserId(request);

            //Put into MDC
            MDC.put("requestId", requestId);
            MDC.put("userId", userId);
            MDC.put("method", request.getMethod());
            MDC.put("path", request.getRequestURI());

            // Add requestId to response header for client-side tracing
            response.addHeader("X-Request-Id", requestId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();// ALWAYS clear — prevents thread pool leaks
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                String token = auth.substring(7);
                return jwtService.extractUserId(token);
            } catch (Exception exception) {
                return "anonymous";
            }
        }
        return "anonymous";
    }
}
