package com.apad.weather.service;

import com.apad.weather.domain.WeatherData;
import com.apad.weather.dto.WeatherRequest;
import reactor.core.publisher.Mono;

/**
 * Weather service interface defining the contract for weather operations.
 * Uses reactive programming with Project Reactor for non-blocking operations.
 */
public interface WeatherService {
    
    /**
     * Get current weather data for the specified request.
     * 
     * @param request the weather request containing location or coordinates
     * @return a Mono containing the weather data
     */
    Mono<WeatherData> getCurrentWeather(WeatherRequest request);
    
    /**
     * Get current weather data by location name.
     * 
     * @param location the location name (city, address, etc.)
     * @return a Mono containing the weather data
     */
    Mono<WeatherData> getCurrentWeatherByLocation(String location);
    
    /**
     * Get current weather data by coordinates.
     * 
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @return a Mono containing the weather data
     */
    Mono<WeatherData> getCurrentWeatherByCoordinates(Double latitude, Double longitude);
    
    /**
     * Check if the weather service is available.
     * 
     * @return a Mono containing true if the service is available, false otherwise
     */
    Mono<Boolean> isServiceAvailable();
}