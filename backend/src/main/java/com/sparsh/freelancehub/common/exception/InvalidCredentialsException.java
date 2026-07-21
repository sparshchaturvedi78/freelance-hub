package com.sparsh.freelancehub.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
