package com.lexgrip.app.platform.service.application.common;

import com.lexgrip.common.api.model.ApiError;
import com.lexgrip.common.api.model.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiError apiError = new ApiError(
                "VALIDATION_ERROR",
                "Request validation failed",
                HttpStatus.BAD_REQUEST,
                Map.of("fields", fieldErrors)
        );

        return ResponseEntity.status(apiError.getStatus()).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> violations = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> violations.put(v.getPropertyPath().toString(), v.getMessage()));

        ApiError apiError = new ApiError(
                "VALIDATION_ERROR",
                "Request validation failed",
                HttpStatus.BAD_REQUEST,
                Map.of("violations", violations)
        );

        return ResponseEntity.status(apiError.getStatus()).body(ApiResponse.error(apiError));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiError apiError = new ApiError(
                "BAD_REQUEST",
                "Malformed request body",
                HttpStatus.BAD_REQUEST
        );

        return ResponseEntity.status(apiError.getStatus()).body(ApiResponse.error(apiError));
    }
}
