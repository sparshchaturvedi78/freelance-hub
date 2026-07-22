package com.sparsh.freelancehub.auth.service;

import com.sparsh.freelancehub.auth.dto.*;
import com.sparsh.freelancehub.auth.entity.RefreshToken;
import com.sparsh.freelancehub.auth.entity.User;
import com.sparsh.freelancehub.auth.repository.RefreshTokenRepository;
import com.sparsh.freelancehub.auth.repository.UserRepository;
import com.sparsh.freelancehub.common.enums.Role;
import com.sparsh.freelancehub.common.exception.EmailAlreadyExistsException;
import com.sparsh.freelancehub.common.exception.InvalidCredentialsException;
import com.sparsh.freelancehub.common.exception.InvalidRefreshTokenException;
import com.sparsh.freelancehub.security.JwtService;
import com.sparsh.freelancehub.security.JwtProperties;
import com.sparsh.freelancehub.tenant.entity.Organization;
import com.sparsh.freelancehub.tenant.repository.OrganizationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                       OrganizationRepository organizationRepository, JwtService jwtService,
                       JwtProperties jwtProperties, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.organizationRepository = organizationRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Organization organization = Organization.builder()
                .name(request.getOrganizationName())
                .build();
        organizationRepository.save(organization);

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.OWNER)
                .organizationId(organization.getId())
                .isActive(true)
                .build();
        userRepository.save(user);

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.getRevokedAt() != null || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        return issueTokens(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getOrganizationId(), user.getRole().name());
        String opaqueRefreshToken = jwtService.generateOpaqueRefreshToken();
        String refreshTokenHash = hashToken(opaqueRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(refreshTokenHash)
                .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .build();
        refreshTokenRepository.save(refreshToken);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .organizationId(user.getOrganizationId())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(opaqueRefreshToken)
                .expiresInSeconds(jwtProperties.getAccessTokenExpirationMs() / 1000)
                .user(userResponse)
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-256 algorithm not found", ex);
        }
    }
}
