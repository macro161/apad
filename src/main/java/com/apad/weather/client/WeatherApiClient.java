package com.apad.weather.client;

import com.apad.weather.domain.WeatherData;
import reactor.core.publisher.Mono;

/**
 * Weather API client interface for external weather service integration.
 * Uses reactive programming with Project Reactor.
 */
public interface WeatherApiClient {
    
    /**
     * Get current weather data for the specified location query.
     * 
     * @param query the location query (name or coordinates)
     * @return a Mono containing the weather data
     */
    Mono<WeatherData> getCurrentWeather(String query);
    
    /**
     * Perform a health check on the weather API service.
     * 
     * @return a Mono containing the health check response
     */
    Mono<String> healthCheck();
}