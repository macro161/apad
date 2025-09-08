package com.apad.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Error response DTO using Java 21 record.
 * Represents API error responses with detailed information.
 */
@Schema(description = "Error response containing error details")
public record ErrorResponse(
        
        @Schema(description = "Error code", example = "WEATHER_API_ERROR")
        String code,
        
        @Schema(description = "Error message", example = "Unable to fetch weather data")
        String message,
        
        @Schema(description = "Detailed error description", example = "The weather service is temporarily unavailable")
        String details,
        
        @Schema(description = "HTTP status code", example = "500")
        Integer status,
        
        @Schema(description = "Request path", example = "/api/weather")
        String path,
        
        @Schema(description = "Timestamp when error occurred")
        LocalDateTime timestamp,
        
        @Schema(description = "Validation errors (if applicable)")
        List<ValidationError> validationErrors
) {
    
    /**
     * Compact constructor with default timestamp
     */
    public ErrorResponse {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    /**
     * Factory method for general errors
     */
    public static ErrorResponse of(String code, String message, Integer status, String path) {
        return new ErrorResponse(code, message, null, status, path, LocalDateTime.now(), null);
    }
    
    /**
     * Factory method for detailed errors
     */
    public static ErrorResponse of(String code, String message, String details, 
                                 Integer status, String path) {
        return new ErrorResponse(code, message, details, status, path, LocalDateTime.now(), null);
    }
    
    /**
     * Factory method for validation errors
     */
    public static ErrorResponse validationError(String message, String path, 
                                              List<ValidationError> validationErrors) {
        return new ErrorResponse(
            "VALIDATION_ERROR", 
            message, 
            "Request validation failed", 
            400, 
            path, 
            LocalDateTime.now(), 
            validationErrors
        );
    }
    
    /**
     * Factory method for weather API errors
     */
    public static ErrorResponse weatherApiError(String message, String path) {
        return new ErrorResponse(
            "WEATHER_API_ERROR", 
            message, 
            "External weather service error", 
            502, 
            path, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * Factory method for location not found errors
     */
    public static ErrorResponse locationNotFound(String location, String path) {
        return new ErrorResponse(
            "LOCATION_NOT_FOUND", 
            String.format("Location '%s' not found", location), 
            "The specified location could not be found", 
            404, 
            path, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * Factory method for rate limit errors
     */
    public static ErrorResponse rateLimitExceeded(String path) {
        return new ErrorResponse(
            "RATE_LIMIT_EXCEEDED", 
            "Rate limit exceeded", 
            "Too many requests. Please try again later", 
            429, 
            path, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * Validation error nested record
     */
    @Schema(description = "Validation error details")
    public record ValidationError(
            @Schema(description = "Field name", example = "latitude")
            String field,
            
            @Schema(description = "Rejected value", example = "95.0")
            Object rejectedValue,
            
            @Schema(description = "Error message", example = "Latitude must be between -90 and 90 degrees")
            String message
    ) {
        
        /**
         * Factory method for field validation errors
         */
        public static ValidationError of(String field, Object rejectedValue, String message) {
            return new ValidationError(field, rejectedValue, message);
        }
    }
}