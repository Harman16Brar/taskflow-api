package com.taskflow_api.auth;

import com.taskflow_api.auth.dto.AuthResponse;
import com.taskflow_api.auth.dto.LoginRequest;
import com.taskflow_api.auth.dto.RefreshTokenRequest;
import com.taskflow_api.auth.dto.RegisterRequest;
import com.taskflow_api.auth.service.AuthService;
import com.taskflow_api.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "User login successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Logged out successfully"));
    }
}
