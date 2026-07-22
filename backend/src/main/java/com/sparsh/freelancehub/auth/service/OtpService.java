package com.sparsh.freelancehub.auth.service;

import com.sparsh.freelancehub.auth.entity.OtpRequest;
import com.sparsh.freelancehub.auth.repository.OtpRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Slf4j
@Service
public class OtpService {
    private final OtpRequestRepository otpRequestRepository;

    @Value("${app.otp.expiry-minutes:10}")
    private Integer otpExpiryMinutes;

    @Value("${app.otp.length:6}")
    private Integer otpLength;

    private static final SecureRandom random = new SecureRandom();

    public OtpService(OtpRequestRepository otpRequestRepository) {
        this.otpRequestRepository = otpRequestRepository;
    }

    @Transactional
    public String generateAndStoreOtp(String email, OtpRequest.OtpPurpose purpose) {
        // Deactivate any existing OTPs for this email and purpose
        otpRequestRepository.deactivateAllForEmailAndPurpose(email, purpose);

        // Generate OTP
        String otp = generateOtp();
        String otpHash = hashOtp(otp);

        // Store OTP request
        OtpRequest otpRequest = OtpRequest.builder()
                .email(email)
                .otpHash(otpHash)
                .purpose(purpose)
                .expiresAt(Instant.now().plusSeconds(otpExpiryMinutes * 60L))
                .isActive(true)
                .attempts(0)
                .maxAttempts(5)
                .build();

        otpRequestRepository.save(otpRequest);
        log.info("OTP generated for {} with purpose {}", email, purpose);

        return otp;
    }

    @Transactional
    public boolean verifyOtp(String email, String otp, OtpRequest.OtpPurpose purpose) {
        OtpRequest otpRequest = otpRequestRepository
                .findByEmailAndPurposeAndIsActiveAndExpiresAtAfter(email, purpose, true, Instant.now())
                .orElse(null);

        if (otpRequest == null) {
            log.warn("No active OTP found for {} with purpose {}", email, purpose);
            return false;
        }

        // Check attempts
        if (otpRequest.getAttempts() >= otpRequest.getMaxAttempts()) {
            otpRequest.setIsActive(false);
            otpRequestRepository.save(otpRequest);
            log.warn("Max OTP verification attempts exceeded for {}", email);
            return false;
        }

        // Increment attempts
        otpRequest.setAttempts(otpRequest.getAttempts() + 1);

        // Verify OTP hash
        String otpHash = hashOtp(otp);
        if (!constantTimeEquals(otpRequest.getOtpHash(), otpHash)) {
            otpRequestRepository.save(otpRequest);
            log.warn("Invalid OTP provided for {}", email);
            return false;
        }

        // Mark as used
        otpRequest.setUsedAt(Instant.now());
        otpRequest.setIsActive(false);
        otpRequestRepository.save(otpRequest);
        log.info("OTP verified successfully for {} with purpose {}", email, purpose);

        return true;
    }

    public Integer getOtpExpiryMinutes() {
        return otpExpiryMinutes;
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
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

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }

        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
