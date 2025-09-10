package com.apad.weather.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Record representing geographical coordinates for weather data requests.
 * Leverages Java 21 record features with validation constraints.
 */
public record Location(
    @NotBlank(message = "City name cannot be blank")
    String city,
    
    @NotBlank(message = "Country code cannot be blank")
    String country,
    
    @NotNull(message = "Latitude cannot be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
    Double latitude,
    
    @NotNull(message = "Longitude cannot be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180 degrees")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180 degrees")
    Double longitude
) {
    
    /**
     * Compact constructor with validation logic using Java 21 features
     */
    public Location {
        if (city != null) {
            city = city.trim();
        }
        if (country != null) {
            country = country.trim().toUpperCase();
        }
    }
    
    /**
     * Factory method for creating location from coordinates only
     */
    public static Location fromCoordinates(double latitude, double longitude) {
        return new Location("Unknown", "XX", latitude, longitude);
    }
    
    /**
     * Factory method for creating location from city and country
     */
    public static Location fromCityCountry(String city, String country) {
        return new Location(city, country, null, null);
    }
    
    /**
     * Check if location has valid coordinates
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Get formatted location string
     */
    public String getFormattedLocation() {
        return switch (hasCoordinates()) {
            case true -> "%s, %s (%.2f, %.2f)".formatted(city, country, latitude, longitude);
            case false -> "%s, %s".formatted(city, country);
        };
    }
}
