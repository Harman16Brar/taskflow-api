package com.taskflow_api.shared.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}
