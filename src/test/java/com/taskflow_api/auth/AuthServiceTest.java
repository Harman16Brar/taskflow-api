package com.taskflow_api.auth;

import com.taskflow_api.auth.dto.AuthResponse;
import com.taskflow_api.auth.dto.LoginRequest;
import com.taskflow_api.auth.dto.RegisterRequest;
import com.taskflow_api.auth.service.AuthService;
import com.taskflow_api.auth.service.JwtService;
import com.taskflow_api.shared.exception.DuplicateResourceException;
import com.taskflow_api.shared.exception.InvalidCredentialsException;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private WorkspaceService workspaceService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest buildRegisterRequest(String email, String password, String firstName, String lastName) {
        RegisterRequest req = new RegisterRequest();
        ReflectionTestUtils.setField(req, "email", email);
        ReflectionTestUtils.setField(req, "password", password);
        ReflectionTestUtils.setField(req, "firstName", firstName);
        ReflectionTestUtils.setField(req, "lastName", lastName);
        return req;
    }

    private LoginRequest buildLoginRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        ReflectionTestUtils.setField(req, "email", email);
        ReflectionTestUtils.setField(req, "password", password);
        return req;
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_whenEmailAlreadyExists_shouldThrowDuplicateResourceException() {
        RegisterRequest request = buildRegisterRequest("duplicate@test.com", "password123", "A", "B");

        when(userRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_withValidData_shouldReturnAuthResponse() {
        RegisterRequest request = buildRegisterRequest("new@test.com", "password123", "Harman", "Brar");

        User saved = User.create("new@test.com", "hashed", "Harman", "Brar");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("new@test.com", response.getEmail());
        verify(workspaceService, times(1)).createDefaultWorkspace(any());
    }

    @Test
    void register_shouldHashPassword() {
        RegisterRequest request = buildRegisterRequest("user@test.com", "plaintext", "A", "B");

        User saved = User.create("user@test.com", "hashed", "A", "B");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtService.generateToken(any())).thenReturn("token");

        authService.register(request);

        verify(passwordEncoder, times(1)).encode("plaintext");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_whenUserNotFound_shouldThrowInvalidCredentialsException() {
        LoginRequest request = buildLoginRequest("ghost@test.com", "pass");
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_whenPasswordMismatch_shouldThrowInvalidCredentialsException() {
        LoginRequest request = buildLoginRequest("user@test.com", "wrongpass");

        User user = User.create("user@test.com", "hashed", "A", "B");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_withValidCredentials_shouldReturnAuthResponseWithBearerToken() {
        LoginRequest request = buildLoginRequest("user@test.com", "password123");

        User user = User.create("user@test.com", "hashed", "Harman", "Brar");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
    }
}
