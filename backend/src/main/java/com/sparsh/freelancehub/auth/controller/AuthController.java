package com.sparsh.freelancehub.auth.controller;

import com.sparsh.freelancehub.auth.dto.*;
import com.sparsh.freelancehub.auth.service.AuthService;
import com.sparsh.freelancehub.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> initiateSignup(@Valid @RequestBody RegisterRequest request) {
        OtpVerificationResponse response = authService.initiateSignup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> resendOtp(@Valid @RequestBody EmailOtpRequest request) {
        OtpVerificationResponse response = authService.resendVerificationOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyEmailAndRegister(
            @Valid @RequestBody VerifyEmailOtpRequest request) {
        AuthResponse response = authService.verifyEmailAndRegister(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> forgotPassword(@Valid @RequestBody EmailOtpRequest request) {
        OtpVerificationResponse response = authService.initiateForgotPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> verifyResetOtp(@Valid @RequestBody VerifyResetOtpRequest request) {
        OtpVerificationResponse response = authService.verifyResetOtp(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        OtpVerificationResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
