package com.taskflow_api.shared.config;

import com.taskflow_api.auth.filter.JwtAuthenticationFilter;
import com.taskflow_api.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserRepository userRepository;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserRepository userRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf((csrf -> csrf.disable()))
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> {})
                        .frameOptions(frame -> frame.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp->csp
                                .policyDirectives("default-src 'self'; frame-ancestors 'none'"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> userRepository.findByEmail(username)
//                .map(user -> org.springframework.security.core.userdetails.User
//                        .withUsername(user.getEmail())
//                        .password(user.getPassword())
//                        .authorities(Collections.emptyList())
//                        .build())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
}
