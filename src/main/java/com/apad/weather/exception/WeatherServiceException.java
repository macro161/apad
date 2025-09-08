package com.apad.weather.exception;

/**
 * Custom exception for weather service operations.
 * Supports retryable error classification using Java 21 features.
 */
public class WeatherServiceException extends RuntimeException {
    
    private final boolean retryable;
    private final String errorCode;
    
    /**
     * Constructor with message only
     */
    public WeatherServiceException(String message) {
        super(message);
        this.retryable = false;
        this.errorCode = "WEATHER_SERVICE_ERROR";
    }
    
    /**
     * Constructor with message and retryable flag
     */
    public WeatherServiceException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
        this.errorCode = "WEATHER_SERVICE_ERROR";
    }
    
    /**
     * Constructor with message, cause, and retryable flag
     */
    public WeatherServiceException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
        this.errorCode = "WEATHER_SERVICE_ERROR";
    }
    
    /**
     * Constructor with message, retryable flag, and error code
     */
    public WeatherServiceException(String message, boolean retryable, String errorCode) {
        super(message);
        this.retryable = retryable;
        this.errorCode = errorCode != null ? errorCode : "WEATHER_SERVICE_ERROR";
    }
    
    /**
     * Constructor with message, cause, retryable flag, and error code
     */
    public WeatherServiceException(String message, Throwable cause, boolean retryable, String errorCode) {
        super(message, cause);
        this.retryable = retryable;
        this.errorCode = errorCode != null ? errorCode : "WEATHER_SERVICE_ERROR";
    }
    
    /**
     * Check if this exception represents a retryable error
     */
    public boolean isRetryable() {
        return retryable;
    }
    
    /**
     * Get the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Factory method for location not found errors
     */
    public static WeatherServiceException locationNotFound(String location) {
        return new WeatherServiceException(
            String.format("Location '%s' not found", location), 
            false, 
            "LOCATION_NOT_FOUND"
        );
    }
    
    /**
     * Factory method for API key errors
     */
    public static WeatherServiceException invalidApiKey() {
        return new WeatherServiceException(
            "Invalid or missing API key", 
            false, 
            "INVALID_API_KEY"
        );
    }
    
    /**
     * Factory method for rate limit errors
     */
    public static WeatherServiceException rateLimitExceeded() {
        return new WeatherServiceException(
            "API rate limit exceeded", 
            true, 
            "RATE_LIMIT_EXCEEDED"
        );
    }
    
    /**
     * Factory method for service unavailable errors
     */
    public static WeatherServiceException serviceUnavailable() {
        return new WeatherServiceException(
            "Weather service is temporarily unavailable", 
            true, 
            "SERVICE_UNAVAILABLE"
        );
    }
    
    /**
     * Factory method for timeout errors
     */
    public static WeatherServiceException timeout() {
        return new WeatherServiceException(
            "Weather service request timed out", 
            true, 
            "REQUEST_TIMEOUT"
        );
    }
}