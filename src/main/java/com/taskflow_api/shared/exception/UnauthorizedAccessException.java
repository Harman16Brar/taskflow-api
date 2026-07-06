package com.taskflow_api.shared.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends AppException {
    public UnauthorizedAccessException() {
        super("You do not have permission to perform this action",
                HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS");
    }
}