package com.taskflow_api.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
}
