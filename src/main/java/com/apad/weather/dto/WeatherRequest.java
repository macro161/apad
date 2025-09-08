package com.apad.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Weather request DTO using Java 21 record with validation annotations.
 * Supports both location name and coordinate-based queries.
 */
@Schema(description = "Weather request containing location information")
public record WeatherRequest(
        
        @Schema(description = "Location name (city, address, etc.)", example = "London")
        @NotBlank(message = "Location cannot be blank when coordinates are not provided")
        String location,
        
        @Schema(description = "Latitude coordinate", example = "51.5074", minimum = "-90", maximum = "90")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
        Double latitude,
        
        @Schema(description = "Longitude coordinate", example = "-0.1278", minimum = "-180", maximum = "180")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180 degrees")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180 degrees")
        Double longitude,
        
        @Schema(description = "Temperature unit preference", example = "celsius", allowableValues = {"celsius", "fahrenheit"})
        @Pattern(regexp = "^(celsius|fahrenheit)$", message = "Temperature unit must be 'celsius' or 'fahrenheit'")
        String temperatureUnit,
        
        @Schema(description = "Wind speed unit preference", example = "kph", allowableValues = {"kph", "mph"})
        @Pattern(regexp = "^(kph|mph)$", message = "Wind speed unit must be 'kph' or 'mph'")
        String windSpeedUnit
) {
    
    /**
     * Compact constructor with custom validation logic
     */
    public WeatherRequest {
        // Set default values
        temperatureUnit = temperatureUnit != null ? temperatureUnit.toLowerCase() : "celsius";
        windSpeedUnit = windSpeedUnit != null ? windSpeedUnit.toLowerCase() : "kph";
        
        // Custom validation: either location or coordinates must be provided
        if ((location == null || location.isBlank()) && 
            (latitude == null || longitude == null)) {
            throw new IllegalArgumentException(
                "Either location name or both latitude and longitude must be provided"
            );
        }
    }
    
    /**
     * Check if request uses coordinates
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Get the query string for the weather API
     */
    public String getQueryString() {
        if (hasCoordinates()) {
            return String.format("%.4f,%.4f", latitude, longitude);
        }
        return location;
    }
    
    /**
     * Check if Celsius is preferred
     */
    public boolean isCelsiusPreferred() {
        return "celsius".equals(temperatureUnit);
    }
    
    /**
     * Check if KPH is preferred for wind speed
     */
    public boolean isKphPreferred() {
        return "kph".equals(windSpeedUnit);
    }
    
    /**
     * Factory method for location-based request
     */
    public static WeatherRequest forLocation(String location) {
        return new WeatherRequest(location, null, null, "celsius", "kph");
    }
    
    /**
     * Factory method for coordinate-based request
     */
    public static WeatherRequest forCoordinates(Double latitude, Double longitude) {
        return new WeatherRequest(null, latitude, longitude, "celsius", "kph");
    }
}