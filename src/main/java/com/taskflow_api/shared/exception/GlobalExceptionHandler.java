package com.taskflow_api.shared.exception;

import com.taskflow_api.shared.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handles all our custom AppExceptions in one place
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleAppException(AppException ex, HttpServletRequest request) {
        log.error("AppException: {} - {}", ex.getErrorCode(), ex.getMessage());

        ApiError error = ApiError.builder()
                .errorCode(ex.getErrorCode())
                .message((ex.getMessage()))
                .status((ex.getStatus()).value())
                .path(request.getRequestURI())
                .timestamp((LocalDateTime.now()))
                .build();
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // Bean validation failures (@Valid on request DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));

        ApiError error = ApiError.builder()
                .errorCode("VALIDATION_FAILED")
                .message("Request validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    // Safety net — nothing should reach here, but log it loudly if it does
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);

        ApiError error = ApiError.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

}
