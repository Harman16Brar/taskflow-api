package com.taskflow_api.shared.error;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ApiError {
    private final String errorCode;
    private final String message;
    private final int status;
    private final String path;
    private final LocalDateTime timestamp;
    private final Map<String, String> fieldErrors;
}
