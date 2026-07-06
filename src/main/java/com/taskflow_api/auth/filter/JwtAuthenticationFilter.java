package com.taskflow_api.auth.filter;

import com.taskflow_api.auth.service.JwtService;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail;

        // 1. Check header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //2 Extract token
        jwt = authHeader.substring(7);

        //3 Extract email from token — skip silently if token is expired or malformed
        try {
            userEmail = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        //4 if email exists and no authentication yet
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByEmail(userEmail).orElse(null);

            //5 Validate token
            if (user != null && jwtService.isTokenValid(jwt, user.getEmail())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                java.util.Collections.emptyList()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //6 Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //7 Continue filter chain
        filterChain.doFilter(request, response);

    }
}
