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
import com.sparsh.freelancehub.security.JwtProperties;
import com.sparsh.freelancehub.security.JwtService;
import com.sparsh.freelancehub.tenant.entity.Organization;
import com.sparsh.freelancehub.tenant.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    public void setup() {
        authService = new AuthService(userRepository, refreshTokenRepository, organizationRepository,
                jwtService, jwtProperties, passwordEncoder);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .organizationName("Test Org")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .build();

        Organization organization = Organization.builder().id(1L).name("Test Org").build();
        User user = User.builder().id(1L).organizationId(1L).email("test@example.com")
                .passwordHash("hashedPassword").role(Role.OWNER).build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(organizationRepository.save(any())).thenReturn(organization);
        when(userRepository.save(any())).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(jwtService.generateAccessToken(anyLong(), anyLong(), anyString())).thenReturn("accessToken");
        when(jwtService.generateOpaqueRefreshToken()).thenReturn("refreshToken");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(organizationRepository, times(1)).save(any());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void testRegisterEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .organizationName("Test Org")
                .email("existing@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder().id(1L).email("existing@example.com").build();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(organizationRepository, never()).save(any());
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder().id(1L).organizationId(1L).email("test@example.com")
                .passwordHash("hashedPassword").role(Role.OWNER).build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateAccessToken(anyLong(), anyLong(), anyString())).thenReturn("accessToken");
        when(jwtService.generateOpaqueRefreshToken()).thenReturn("refreshToken");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals(1L, response.getUser().getId());
    }

    @Test
    public void testLoginInvalidEmail() {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    public void testLoginInvalidPassword() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User user = User.builder().id(1L).email("test@example.com")
                .passwordHash("hashedPassword").build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    public void testRefreshTokenSuccess() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("validRefreshToken")
                .build();

        User user = User.builder().id(1L).organizationId(1L).email("test@example.com")
                .role(Role.OWNER).build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .tokenHash("hashedToken")
                .expiresAt(Instant.now().plusSeconds(10000))
                .build();

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(refreshToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(anyLong(), anyLong(), anyString())).thenReturn("newAccessToken");
        when(jwtService.generateOpaqueRefreshToken()).thenReturn("newRefreshToken");
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(604800000L);
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.refresh(request);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        verify(refreshTokenRepository, times(2)).save(any());
    }

    @Test
    public void testRefreshTokenInvalid() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalidRefreshToken")
                .build();

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refresh(request));
    }

    @Test
    public void testLogoutSuccess() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("validRefreshToken")
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .userId(1L)
                .tokenHash("hashedToken")
                .build();

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(refreshToken));

        authService.logout(request);

        verify(refreshTokenRepository, times(1)).save(any());
    }
}
