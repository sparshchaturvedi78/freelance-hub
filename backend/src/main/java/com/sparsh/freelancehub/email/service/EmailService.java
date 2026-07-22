package com.sparsh.freelancehub.email.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    @Value("${app.mail.from:noreply@freelancehub.local}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private Boolean mailEnabled;

    public EmailService(JavaMailSender mailSender, EmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
    }

    public void sendEmailVerificationOtp(String toEmail, String fullName, String otp, int expiryMinutes) {
        if (!mailEnabled) {
            log.warn("Email sending is disabled. OTP for {}: {}", toEmail, otp);
            return;
        }

        try {
            String htmlContent = emailTemplateService.generateEmailVerificationTemplate(fullName, otp, expiryMinutes);
            sendHtmlEmail(toEmail, "Verify Your FreelanceHub Email", htmlContent);
            log.info("Email verification OTP sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email verification OTP to {}", toEmail, e);
            throw new RuntimeException("Failed to send email verification OTP", e);
        }
    }

    public void sendPasswordResetOtp(String toEmail, String fullName, String otp, int expiryMinutes) {
        if (!mailEnabled) {
            log.warn("Email sending is disabled. Password reset OTP for {}: {}", toEmail, otp);
            return;
        }

        try {
            String htmlContent = emailTemplateService.generatePasswordResetTemplate(fullName, otp, expiryMinutes);
            sendHtmlEmail(toEmail, "Reset Your FreelanceHub Password", htmlContent);
            log.info("Password reset OTP sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset OTP to {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset OTP", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
