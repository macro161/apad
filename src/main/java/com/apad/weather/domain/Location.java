package com.apad.weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Location domain model using Java 21 record.
 * Represents a geographical location with coordinates and address information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Location(
        String name,
        String country,
        String region,
        Double latitude,
        Double longitude,
        String timezone
) {
    
    /**
     * Compact constructor with validation using Java 21 features
     */
    public Location {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be null or blank");
        }
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
    }
    
    /**
     * Factory method for creating location from coordinates
     */
    public static Location fromCoordinates(Double latitude, Double longitude) {
        return new Location(
            String.format("%.2f,%.2f", latitude, longitude),
            null,
            null,
            latitude,
            longitude,
            null
        );
    }
    
    /**
     * Check if location has valid coordinates
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Get formatted coordinates string
     */
    public String getCoordinatesString() {
        return hasCoordinates() ? 
            String.format("%.4f,%.4f", latitude, longitude) : 
            null;
    }
}