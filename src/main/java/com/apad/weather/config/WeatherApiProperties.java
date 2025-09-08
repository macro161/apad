package com.apad.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Weather API integration.
 * Uses Spring Boot configuration properties binding.
 */
@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiProperties {
    
    private String baseUrl = "https://api.weatherapi.com/v1";
    private String apiKey;
    private int timeoutSeconds = 10;
    private int maxRetries = 3;
    private boolean cacheEnabled = true;
    private int cacheTtlMinutes = 10;
    
    /**
     * Get the base URL for the weather API
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Set the base URL for the weather API
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Get the API key for authentication
     */
    public String getApiKey() {
        return apiKey;
    }
    
    /**
     * Set the API key for authentication
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    /**
     * Get the request timeout in seconds
     */
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    /**
     * Set the request timeout in seconds
     */
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    /**
     * Get the maximum number of retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * Set the maximum number of retries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    /**
     * Check if caching is enabled
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    /**
     * Set whether caching is enabled
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
    
    /**
     * Get the cache TTL in minutes
     */
    public int getCacheTtlMinutes() {
        return cacheTtlMinutes;
    }
    
    /**
     * Set the cache TTL in minutes
     */
    public void setCacheTtlMinutes(int cacheTtlMinutes) {
        this.cacheTtlMinutes = cacheTtlMinutes;
    }
    
    /**
     * Validate the configuration
     */
    public boolean isValid() {
        return apiKey != null && !apiKey.isBlank() && 
               baseUrl != null && !baseUrl.isBlank() &&
               timeoutSeconds > 0 && maxRetries >= 0;
    }
}