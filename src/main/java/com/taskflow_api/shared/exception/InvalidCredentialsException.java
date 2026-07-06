package com.taskflow_api.shared.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AppException {
    public InvalidCredentialsException() {
        super("Email or password is incorrect",
                HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}
