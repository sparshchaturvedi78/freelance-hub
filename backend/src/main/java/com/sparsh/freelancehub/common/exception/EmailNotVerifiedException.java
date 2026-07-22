package com.sparsh.freelancehub.common.exception;

import org.springframework.http.HttpStatus;

public class EmailNotVerifiedException extends ApiException {
    public EmailNotVerifiedException(String email) {
        super("Email " + email + " is not verified. Please verify your email before logging in.", HttpStatus.FORBIDDEN);
    }
}
