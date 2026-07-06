package com.taskflow_api.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(resource + " not found: " + identifier,
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }
}
