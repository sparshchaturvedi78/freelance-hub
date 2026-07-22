package com.sparsh.freelancehub.common.exception;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends ApiException {
    public PasswordMismatchException() {
        super("Passwords do not match", HttpStatus.BAD_REQUEST);
    }
}
