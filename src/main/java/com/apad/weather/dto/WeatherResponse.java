package com.apad.weather.dto;

import com.apad.weather.domain.WeatherData;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Weather response DTO using Java 21 record.
 * Represents the API response with weather information.
 */
@Schema(description = "Weather response containing current weather information")
public record WeatherResponse(
        
        @Schema(description = "Location information")
        LocationDto location,
        
        @Schema(description = "Current weather condition")
        ConditionDto condition,
        
        @Schema(description = "Current temperature", example = "22.5")
        Double temperature,
        
        @Schema(description = "Temperature unit", example = "celsius")
        String temperatureUnit,
        
        @Schema(description = "Feels like temperature", example = "24.1")
        Double feelsLike,
        
        @Schema(description = "Humidity percentage", example = "65")
        Integer humidity,
        
        @Schema(description = "Wind speed", example = "15.2")
        Double windSpeed,
        
        @Schema(description = "Wind speed unit", example = "kph")
        String windSpeedUnit,
        
        @Schema(description = "Wind direction", example = "NW")
        String windDirection,
        
        @Schema(description = "Atmospheric pressure in millibars", example = "1013.2")
        Double pressure,
        
        @Schema(description = "Precipitation amount", example = "0.5")
        Double precipitation,
        
        @Schema(description = "Visibility in kilometers", example = "10.0")
        Double visibility,
        
        @Schema(description = "UV index", example = "5.2")
        Double uvIndex,
        
        @Schema(description = "Last updated timestamp")
        LocalDateTime lastUpdated
) {
    
    /**
     * Factory method to create response from domain model
     */
    public static WeatherResponse from(WeatherData weatherData, WeatherRequest request) {
        return new WeatherResponse(
            LocationDto.from(weatherData.location()),
            ConditionDto.from(weatherData.condition()),
            weatherData.getTemperature(
                request.isCelsiusPreferred() ? 
                    WeatherData.TemperatureUnit.CELSIUS : 
                    WeatherData.TemperatureUnit.FAHRENHEIT
            ),
            request.temperatureUnit(),
            weatherData.getFeelsLike(
                request.isCelsiusPreferred() ? 
                    WeatherData.TemperatureUnit.CELSIUS : 
                    WeatherData.TemperatureUnit.FAHRENHEIT
            ),
            weatherData.humidity(),
            weatherData.getWindSpeed(
                request.isKphPreferred() ? 
                    WeatherData.SpeedUnit.KPH : 
                    WeatherData.SpeedUnit.MPH
            ),
            request.windSpeedUnit(),
            weatherData.windDirection(),
            weatherData.pressureMb(),
            weatherData.precipitationMm(),
            weatherData.visibilityKm(),
            weatherData.uvIndex(),
            weatherData.lastUpdated()
        );
    }
    
    /**
     * Location DTO nested record
     */
    @Schema(description = "Location information")
    public record LocationDto(
            @Schema(description = "Location name", example = "London")
            String name,
            
            @Schema(description = "Country", example = "United Kingdom")
            String country,
            
            @Schema(description = "Region/State", example = "City of London, Greater London")
            String region,
            
            @Schema(description = "Latitude", example = "51.52")
            Double latitude,
            
            @Schema(description = "Longitude", example = "-0.11")
            Double longitude,
            
            @Schema(description = "Timezone", example = "Europe/London")
            String timezone
    ) {
        public static LocationDto from(com.apad.weather.domain.Location location) {
            return new LocationDto(
                location.name(),
                location.country(),
                location.region(),
                location.latitude(),
                location.longitude(),
                location.timezone()
            );
        }
    }
    
    /**
     * Condition DTO nested record
     */
    @Schema(description = "Weather condition information")
    public record ConditionDto(
            @Schema(description = "Weather condition text", example = "Partly cloudy")
            String text,
            
            @Schema(description = "Weather condition icon URL", example = "//cdn.weatherapi.com/weather/64x64/day/116.png")
            String icon,
            
            @Schema(description = "Weather condition code", example = "1003")
            Integer code
    ) {
        public static ConditionDto from(com.apad.weather.domain.WeatherCondition condition) {
            return new ConditionDto(
                condition.text(),
                condition.icon(),
                condition.code()
            );
        }
    }
}