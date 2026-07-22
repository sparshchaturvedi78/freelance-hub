package com.sparsh.freelancehub.email.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateService {

    public String generateEmailVerificationTemplate(String fullName, String otp, int expiryMinutes) {
        return buildEmailTemplate(
            fullName,
            "Welcome to FreelanceHub!",
            getEmailVerificationBody(otp, expiryMinutes)
        );
    }

    public String generatePasswordResetTemplate(String fullName, String otp, int expiryMinutes) {
        return buildEmailTemplate(
            fullName,
            "Reset Your FreelanceHub Password",
            getPasswordResetBody(otp, expiryMinutes)
        );
    }

    private String getEmailVerificationBody(String otp, int expiryMinutes) {
        return String.format("""
            <p>To complete your account creation, please verify your email address using the OTP below:</p>

            <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0;">
                <h2 style="font-family: monospace; font-size: 32px; letter-spacing: 4px; color: #333; margin: 0;">%s</h2>
            </div>

            <p><strong>This OTP is valid for %d minutes.</strong></p>

            <p style="color: #d32f2f; font-weight: bold;">🔒 Security Warning:</p>
            <p>If you did not request this verification, you can safely ignore this email. Your account will not be created until you verify your email.</p>
            """, otp, expiryMinutes);
    }

    private String getPasswordResetBody(String otp, int expiryMinutes) {
        return String.format("""
            <p>We received a request to reset your FreelanceHub password.</p>

            <p>Use the OTP below to continue with your password reset:</p>

            <div style="background-color: #f5f5f5; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0;">
                <h2 style="font-family: monospace; font-size: 32px; letter-spacing: 4px; color: #333; margin: 0;">%s</h2>
            </div>

            <p><strong>This OTP is valid for %d minutes.</strong></p>

            <p style="color: #d32f2f; font-weight: bold;">🔒 Security Warning:</p>
            <p>If you did not request a password reset, please ignore this email. Your password will not be changed unless you complete the reset process.</p>
            """, otp, expiryMinutes);
    }

    private String buildEmailTemplate(String fullName, String subject, String body) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f9f9f9;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 30px 20px;
                        text-align: center;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        margin: 0 0 10px 0;
                    }
                    .header-subtitle {
                        font-size: 14px;
                        opacity: 0.9;
                        margin: 0;
                    }
                    .content {
                        padding: 30px;
                    }
                    .greeting {
                        font-size: 18px;
                        font-weight: 600;
                        margin-bottom: 15px;
                    }
                    .footer {
                        background-color: #f5f5f5;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #666;
                        border-top: 1px solid #eee;
                    }
                    .footer p {
                        margin: 5px 0;
                    }
                    a {
                        color: #667eea;
                        text-decoration: none;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">💼 FreelanceHub</div>
                        <p class="header-subtitle">Professional Freelance Management</p>
                    </div>

                    <div class="content">
                        <div class="greeting">Hello %s,</div>

                        %s
                    </div>

                    <div class="footer">
                        <p><strong>FreelanceHub © 2025</strong></p>
                        <p>This is an automated email. Please do not reply to this message.</p>
                        <p>If you have any questions, contact our support team.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, body);
    }
}
