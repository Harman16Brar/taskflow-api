package com.taskflow_api.auth.service;

import com.taskflow_api.auth.RefreshToken;
import com.taskflow_api.auth.RefreshTokenService;
import com.taskflow_api.auth.dto.AuthResponse;
import com.taskflow_api.auth.dto.LoginRequest;
import com.taskflow_api.auth.dto.RegisterRequest;
import com.taskflow_api.shared.email.EmailService;
import com.taskflow_api.shared.exception.AppException;
import com.taskflow_api.shared.exception.DuplicateResourceException;
import com.taskflow_api.shared.exception.InvalidCredentialsException;
import com.taskflow_api.user.User;
import com.taskflow_api.user.UserRepository;
import com.taskflow_api.workspace.WorkspaceService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceService workspaceService;
    private final EmailService emailService;
    private final MeterRegistry meterRegistry;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       WorkspaceService workspaceService,
                       EmailService emailService,
                       MeterRegistry meterRegistry,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.workspaceService = workspaceService;
        this.emailService = emailService;
        this.meterRegistry = meterRegistry;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        User user = User.create(registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName());

        User savdUser = userRepository.save(user);

        workspaceService.createDefaultWorkspace(savdUser);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        meterRegistry.counter("taskflow.users.registered").increment();

        String refreshToken = refreshTokenService.createRefreshToken(savdUser.getId());

        return AuthResponse.builder()
                .userId(savdUser.getId())
                .email(savdUser.getEmail())
                .firstName(savdUser.getFirstName())
                .lastName(savdUser.getLastName())
                .accessToken(jwtService.generateToken(savdUser))
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();

    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException();

        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(jwtService.generateToken(user))
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse refresh(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.validate(rawRefreshToken);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new AppException(
                        "User not found",
                        HttpStatus.UNAUTHORIZED,
                        "USER_NOT_FOUND"
                ));

        //generate new Accesstoken
        //generate new refresh token
        String newAccessToken = jwtService.generateToken(user);

        String newRefreshToken = refreshTokenService.rotate(rawRefreshToken, user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }
}

