package com.apad.weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Main weather data domain model using Java 21 record.
 * Represents complete weather information for a location.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherData(
        Location location,
        WeatherCondition condition,
        Double temperatureCelsius,
        Double temperatureFahrenheit,
        Double feelsLikeCelsius,
        Double feelsLikeFahrenheit,
        Integer humidity,
        Double windSpeedKph,
        Double windSpeedMph,
        Integer windDegree,
        String windDirection,
        Double pressureMb,
        Double pressureIn,
        Double precipitationMm,
        Double precipitationIn,
        Double visibilityKm,
        Double visibilityMiles,
        Double uvIndex,
        LocalDateTime lastUpdated
) {
    
    /**
     * Compact constructor with validation using Java 21 features
     */
    public WeatherData {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (condition == null) {
            throw new IllegalArgumentException("Weather condition cannot be null");
        }
        if (humidity != null && (humidity < 0 || humidity > 100)) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100");
        }
        if (uvIndex != null && uvIndex < 0) {
            throw new IllegalArgumentException("UV index cannot be negative");
        }
    }
    
    /**
     * Factory method for creating weather data with minimal information
     */
    public static WeatherData minimal(Location location, WeatherCondition condition, 
                                    Double temperatureCelsius) {
        return new WeatherData(
            location,
            condition,
            temperatureCelsius,
            celsiusToFahrenheit(temperatureCelsius),
            null, null, null, null, null, null, null,
            null, null, null, null, null, null, null,
            LocalDateTime.now()
        );
    }
    
    /**
     * Convert Celsius to Fahrenheit
     */
    public static Double celsiusToFahrenheit(Double celsius) {
        return celsius != null ? (celsius * 9.0 / 5.0) + 32.0 : null;
    }
    
    /**
     * Convert Fahrenheit to Celsius
     */
    public static Double fahrenheitToCelsius(Double fahrenheit) {
        return fahrenheit != null ? (fahrenheit - 32.0) * 5.0 / 9.0 : null;
    }
    
    /**
     * Check if weather data is recent (within last hour)
     */
    public boolean isRecent() {
        return lastUpdated != null && 
               lastUpdated.isAfter(LocalDateTime.now().minusHours(1));
    }
    
    /**
     * Get temperature in preferred unit
     */
    public Double getTemperature(TemperatureUnit unit) {
        return switch (unit) {
            case CELSIUS -> temperatureCelsius;
            case FAHRENHEIT -> temperatureFahrenheit;
        };
    }
    
    /**
     * Get feels like temperature in preferred unit
     */
    public Double getFeelsLike(TemperatureUnit unit) {
        return switch (unit) {
            case CELSIUS -> feelsLikeCelsius;
            case FAHRENHEIT -> feelsLikeFahrenheit;
        };
    }
    
    /**
     * Get wind speed in preferred unit
     */
    public Double getWindSpeed(SpeedUnit unit) {
        return switch (unit) {
            case KPH -> windSpeedKph;
            case MPH -> windSpeedMph;
        };
    }
    
    /**
     * Get precipitation in preferred unit
     */
    public Double getPrecipitation(LengthUnit unit) {
        return switch (unit) {
            case MM -> precipitationMm;
            case INCHES -> precipitationIn;
        };
    }
    
    /**
     * Get visibility in preferred unit
     */
    public Double getVisibility(LengthUnit unit) {
        return switch (unit) {
            case MM -> visibilityKm != null ? visibilityKm * 1000 : null;
            case INCHES -> visibilityMiles != null ? visibilityMiles * 63360 : null;
        };
    }
    
    /**
     * Get pressure in preferred unit
     */
    public Double getPressure(PressureUnit unit) {
        return switch (unit) {
            case MB -> pressureMb;
            case INCHES -> pressureIn;
        };
    }
    
    /**
     * Temperature unit enumeration
     */
    public enum TemperatureUnit {
        CELSIUS, FAHRENHEIT
    }
    
    /**
     * Speed unit enumeration
     */
    public enum SpeedUnit {
        KPH, MPH
    }
    
    /**
     * Length unit enumeration
     */
    public enum LengthUnit {
        MM, INCHES
    }
    
    /**
     * Pressure unit enumeration
     */
    public enum PressureUnit {
        MB, INCHES
    }
}