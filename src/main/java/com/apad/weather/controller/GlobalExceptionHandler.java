package com.apad.weather.controller;

import com.apad.weather.dto.ErrorResponse;
import com.apad.weather.exception.WeatherServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the weather application.
 * Provides consistent error responses using Java 21 features.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle WeatherServiceException using Java 21 pattern matching
     */
    @ExceptionHandler(WeatherServiceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWeatherServiceException(
            WeatherServiceException ex, ServerWebExchange exchange) {
        
        logger.error("Weather service exception: {}", ex.getMessage(), ex);
        
        var status = switch (ex.getErrorCode()) {
            case "LOCATION_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "INVALID_API_KEY" -> HttpStatus.UNAUTHORIZED;
            case "RATE_LIMIT_EXCEEDED" -> HttpStatus.TOO_MANY_REQUESTS;
            case "SERVICE_UNAVAILABLE", "REQUEST_TIMEOUT" -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        
        var errorResponse = ErrorResponse.of(
            ex.getErrorCode(),
            ex.getMessage(),
            getErrorDetails(ex),
            status.value(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
    
    /**
     * Handle validation exceptions from request body validation
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        
        logger.warn("Validation exception: {}", ex.getMessage());
        
        var validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> ErrorResponse.ValidationError.of(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        var errorResponse = ErrorResponse.validationError(
            "Request validation failed",
            exchange.getRequest().getPath().value(),
            validationErrors
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    /**
     * Handle validation exceptions from method parameter validation
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex, ServerWebExchange exchange) {
        
        logger.warn("Constraint violation exception: {}", ex.getMessage());
        
        var validationErrors = ex.getConstraintViolations().stream()
            .map(this::mapConstraintViolation)
            .collect(Collectors.toList());
        
        var errorResponse = ErrorResponse.validationError(
            "Parameter validation failed",
            exchange.getRequest().getPath().value(),
            validationErrors
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    /**
     * Handle method argument validation exceptions (for @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, ServerWebExchange exchange) {
        
        logger.warn("Method argument validation exception: {}", ex.getMessage());
        
        var validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> ErrorResponse.ValidationError.of(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        var errorResponse = ErrorResponse.validationError(
            "Request validation failed",
            exchange.getRequest().getPath().value(),
            validationErrors
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        
        logger.warn("Illegal argument exception: {}", ex.getMessage());
        
        var errorResponse = ErrorResponse.of(
            "INVALID_ARGUMENT",
            ex.getMessage(),
            "The provided argument is invalid",
            HttpStatus.BAD_REQUEST.value(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        
        logger.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        var errorResponse = ErrorResponse.of(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            "Please try again later or contact support if the problem persists",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exchange.getRequest().getPath().value()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    /**
     * Map constraint violation to validation error
     */
    private ErrorResponse.ValidationError mapConstraintViolation(ConstraintViolation<?> violation) {
        var propertyPath = violation.getPropertyPath().toString();
        var fieldName = propertyPath.contains(".") ? 
            propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : 
            propertyPath;
        
        return ErrorResponse.ValidationError.of(
            fieldName,
            violation.getInvalidValue(),
            violation.getMessage()
        );
    }
    
    /**
     * Get error details based on exception type using Java 21 pattern matching
     */
    private String getErrorDetails(WeatherServiceException ex) {
        return switch (ex.getErrorCode()) {
            case "LOCATION_NOT_FOUND" -> "The specified location could not be found. Please check the location name or coordinates.";
            case "INVALID_API_KEY" -> "The API key is invalid or missing. Please check your configuration.";
            case "RATE_LIMIT_EXCEEDED" -> "Too many requests have been made. Please wait before making another request.";
            case "SERVICE_UNAVAILABLE" -> "The weather service is temporarily unavailable. Please try again later.";
            case "REQUEST_TIMEOUT" -> "The request to the weather service timed out. Please try again.";
            default -> "An error occurred while processing your weather request.";
        };
    }
}