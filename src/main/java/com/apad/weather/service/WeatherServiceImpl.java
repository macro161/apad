package com.apad.weather.service;

import com.apad.weather.client.WeatherApiClient;
import com.apad.weather.domain.WeatherData;
import com.apad.weather.dto.WeatherRequest;
import com.apad.weather.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Weather service implementation with caching, retry logic, and comprehensive error handling.
 * Uses Java 21 features and reactive programming patterns.
 */
@Service
public class WeatherServiceImpl implements WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    
    private final WeatherApiClient weatherApiClient;
    
    public WeatherServiceImpl(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }
    
    @Override
    @Cacheable(value = "weather", key = "#request.getQueryString()")
    public Mono<WeatherData> getCurrentWeather(WeatherRequest request) {
        logger.info("Fetching weather data for: {}", request.getQueryString());
        
        return weatherApiClient.getCurrentWeather(request.getQueryString())
            .doOnSuccess(data -> logger.info("Successfully fetched weather data for: {}", 
                request.getQueryString()))
            .doOnError(error -> logger.error("Failed to fetch weather data for: {}", 
                request.getQueryString(), error))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(10))
                .filter(this::isRetryableError))
            .onErrorMap(this::mapToWeatherServiceException);
    }
    
    @Override
    public Mono<WeatherData> getCurrentWeatherByLocation(String location) {
        if (location == null || location.isBlank()) {
            return Mono.error(new WeatherServiceException("Location cannot be null or blank"));
        }
        
        WeatherRequest request = WeatherRequest.forLocation(location);
        return getCurrentWeather(request);
    }
    
    @Override
    public Mono<WeatherData> getCurrentWeatherByCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return Mono.error(new WeatherServiceException("Latitude and longitude cannot be null"));
        }
        
        if (latitude < -90 || latitude > 90) {
            return Mono.error(new WeatherServiceException("Latitude must be between -90 and 90 degrees"));
        }
        
        if (longitude < -180 || longitude > 180) {
            return Mono.error(new WeatherServiceException("Longitude must be between -180 and 180 degrees"));
        }
        
        WeatherRequest request = WeatherRequest.forCoordinates(latitude, longitude);
        return getCurrentWeather(request);
    }
    
    @Override
    public Mono<Boolean> isServiceAvailable() {
        logger.debug("Checking weather service availability");
        
        return weatherApiClient.healthCheck()
            .map(response -> true)
            .doOnSuccess(available -> logger.debug("Weather service availability: {}", available))
            .onErrorReturn(false)
            .timeout(Duration.ofSeconds(5));
    }
    
    /**
     * Determine if an error is retryable using Java 21 pattern matching
     */
    private boolean isRetryableError(Throwable error) {
        return switch (error) {
            case java.net.ConnectException ce -> true;
            case java.util.concurrent.TimeoutException te -> true;
            case org.springframework.web.reactive.function.client.WebClientRequestException wcre -> true;
            case WeatherServiceException wse when wse.isRetryable() -> true;
            default -> false;
        };
    }
    
    /**
     * Map various exceptions to WeatherServiceException using Java 21 pattern matching
     */
    private WeatherServiceException mapToWeatherServiceException(Throwable error) {
        return switch (error) {
            case WeatherServiceException wse -> wse;
            case java.net.ConnectException ce -> 
                new WeatherServiceException("Unable to connect to weather service", ce, true);
            case java.util.concurrent.TimeoutException te -> 
                new WeatherServiceException("Weather service request timed out", te, true);
            case org.springframework.web.reactive.function.client.WebClientResponseException wcre -> {
                if (wcre.getStatusCode().is4xxClientError()) {
                    yield new WeatherServiceException(
                        "Invalid request to weather service: " + wcre.getMessage(), wcre, false);
                } else {
                    yield new WeatherServiceException(
                        "Weather service error: " + wcre.getMessage(), wcre, true);
                }
            }
            case org.springframework.web.reactive.function.client.WebClientRequestException wcre -> 
                new WeatherServiceException("Weather service request failed", wcre, true);
            default -> new WeatherServiceException("Unexpected error occurred", error, false);
        };
    }
}