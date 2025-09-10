package com.apad.weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Weather condition domain model using Java 21 record.
 * Represents the current weather condition with description and icon.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherCondition(
        String text,
        String icon,
        Integer code
) {
    
    /**
     * Compact constructor with validation
     */
    public WeatherCondition {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Weather condition text cannot be null or blank");
        }
    }
    
    /**
     * Check if this is a clear/sunny condition
     */
    public boolean isClear() {
        return text != null && 
               (text.toLowerCase().contains("clear") || 
                text.toLowerCase().contains("sunny"));
    }
    
    /**
     * Check if this is a rainy condition
     */
    public boolean isRainy() {
        return text != null && 
               (text.toLowerCase().contains("rain") || 
                text.toLowerCase().contains("drizzle") ||
                text.toLowerCase().contains("shower"));
    }
    
    /**
     * Check if this is a snowy condition
     */
    public boolean isSnowy() {
        return text != null && 
               (text.toLowerCase().contains("snow") || 
                text.toLowerCase().contains("blizzard"));
    }
    
    /**
     * Check if this is a cloudy condition
     */
    public boolean isCloudy() {
        return text != null && 
               (text.toLowerCase().contains("cloud") || 
                text.toLowerCase().contains("overcast"));
    }
}