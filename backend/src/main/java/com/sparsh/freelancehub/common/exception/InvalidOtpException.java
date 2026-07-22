package com.sparsh.freelancehub.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends ApiException {
    public InvalidOtpException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidOtpException() {
        super("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
    }
}
