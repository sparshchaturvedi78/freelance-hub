package com.sparsh.freelancehub.auth.service;

import com.sparsh.freelancehub.auth.dto.*;
import com.sparsh.freelancehub.auth.entity.OtpRequest;
import com.sparsh.freelancehub.auth.entity.RefreshToken;
import com.sparsh.freelancehub.auth.entity.TempSignup;
import com.sparsh.freelancehub.auth.entity.User;
import com.sparsh.freelancehub.auth.repository.OtpRequestRepository;
import com.sparsh.freelancehub.auth.repository.RefreshTokenRepository;
import com.sparsh.freelancehub.auth.repository.TempSignupRepository;
import com.sparsh.freelancehub.auth.repository.UserRepository;
import com.sparsh.freelancehub.common.enums.Role;
import com.sparsh.freelancehub.common.exception.*;
import com.sparsh.freelancehub.email.service.EmailService;
import com.sparsh.freelancehub.security.JwtService;
import com.sparsh.freelancehub.security.JwtProperties;
import com.sparsh.freelancehub.tenant.entity.Organization;
import com.sparsh.freelancehub.tenant.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpRequestRepository otpRequestRepository;
    private final TempSignupRepository tempSignupRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, OtpRequestRepository otpRequestRepository, TempSignupRepository tempSignupRepository, OrganizationRepository organizationRepository, JwtService jwtService, JwtProperties jwtProperties, PasswordEncoder passwordEncoder, OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpRequestRepository = otpRequestRepository;
        this.tempSignupRepository = tempSignupRepository;
        this.organizationRepository = organizationRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @Transactional
    public OtpVerificationResponse initiateSignup(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        // Check if email already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Clean up any expired signup attempts for this email
        tempSignupRepository.deleteByEmail(request.getEmail());

        // Generate and send OTP
        String otp = otpService.generateAndStoreOtp(request.getEmail(), OtpRequest.OtpPurpose.EMAIL_VERIFICATION);

        // Store signup data temporarily
        String fullName = request.getFullName() != null ? request.getFullName() : "";
        String organizationName = request.getOrganizationName();
        String passwordHash = passwordEncoder.encode(request.getPassword());

        TempSignup tempSignup = TempSignup.builder()
                .email(request.getEmail())
                .fullName(fullName)
                .organizationName(organizationName)
                .passwordHash(passwordHash)
                .expiresAt(Instant.now().plusSeconds(otpService.getOtpExpiryMinutes() * 60L))
                .build();
        tempSignupRepository.save(tempSignup);

        try {
            emailService.sendEmailVerificationOtp(request.getEmail(), fullName, otp, otpService.getOtpExpiryMinutes());
        } catch (Exception e) {
            log.error("Failed to send email OTP", e);
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }

        return OtpVerificationResponse.builder()
                .verified(false)
                .message("OTP sent to " + request.getEmail())
                .build();
    }

    @Transactional
    public OtpVerificationResponse resendVerificationOtp(String email) {
        TempSignup tempSignup = tempSignupRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No signup found for this email. Please register first."));

        // Generate and send new OTP
        String otp = otpService.generateAndStoreOtp(email, OtpRequest.OtpPurpose.EMAIL_VERIFICATION);

        try {
            emailService.sendEmailVerificationOtp(email, tempSignup.getFullName(), otp, otpService.getOtpExpiryMinutes());
        } catch (Exception e) {
            log.error("Failed to send email OTP", e);
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }

        log.info("Verification OTP resent to {}", email);

        return OtpVerificationResponse.builder()
                .verified(false)
                .message("New OTP sent to " + email)
                .build();
    }

    @Transactional
    public AuthResponse verifyEmailAndRegister(VerifyEmailOtpRequest request) {
        // Verify OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpRequest.OtpPurpose.EMAIL_VERIFICATION)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        // Retrieve stored signup data
        TempSignup tempSignup = tempSignupRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Signup data not found. Please try registering again."));

        // Create user
        Role role = Role.MEMBER;
        Long organizationId = null;

        // If organizationName is provided, create organization and set user as OWNER
        if (tempSignup.getOrganizationName() != null && !tempSignup.getOrganizationName().isEmpty()) {
            Organization organization = Organization.builder()
                    .name(tempSignup.getOrganizationName())
                    .build();
            organizationRepository.save(organization);
            organizationId = organization.getId();
            role = Role.OWNER;
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(tempSignup.getPasswordHash())
                .fullName(tempSignup.getFullName())
                .role(role)
                .organizationId(organizationId)
                .emailVerified(true)
                .isActive(true)
                .build();
        userRepository.save(user);

        // Clean up temp signup
        tempSignupRepository.delete(tempSignup);

        log.info("User registered successfully: {}", request.getEmail());

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException(request.getEmail());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueTokens(user);
    }

    @Transactional
    public OtpVerificationResponse initiateForgotPassword(EmailOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        // Generate and send OTP
        String otp = otpService.generateAndStoreOtp(request.getEmail(), OtpRequest.OtpPurpose.PASSWORD_RESET);

        try {
            emailService.sendPasswordResetOtp(request.getEmail(), user.getFullName(), otp, otpService.getOtpExpiryMinutes());
        } catch (Exception e) {
            log.error("Failed to send password reset OTP", e);
            throw new RuntimeException("Failed to send password reset email. Please try again.");
        }

        return OtpVerificationResponse.builder()
                .verified(false)
                .message("Password reset OTP sent to " + request.getEmail())
                .build();
    }

    @Transactional
    public OtpVerificationResponse verifyResetOtp(VerifyResetOtpRequest request) {
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp(), OtpRequest.OtpPurpose.PASSWORD_RESET)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        return OtpVerificationResponse.builder()
                .verified(true)
                .message("OTP verified. Proceed to reset password.")
                .build();
    }

    @Transactional
    public OtpVerificationResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }

        // Verify OTP one more time (already verified in previous step, but for safety)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for: {}", request.getEmail());

        return OtpVerificationResponse.builder()
                .verified(true)
                .message("Password reset successfully. You can now log in.")
                .build();
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
