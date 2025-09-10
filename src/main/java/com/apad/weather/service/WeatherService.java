package com.apad.weather.service;

import com.apad.weather.dto.WeatherRequest;
import com.apad.weather.dto.WeatherResponse;
import com.apad.weather.model.Weather;
import com.apad.weather.repository.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    @Autowired
    private WeatherRepository weatherRepository;
    
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Value("${weather.api.key:demo_key}")
    private String apiKey;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5}")
    private String apiUrl;
    
    /**
     * Get current weather for a location
     */
    public WeatherResponse getCurrentWeather(String location) {
        logger.info("Getting current weather for location: {}", location);
        
        try {
            // First try to get from external API
            Weather weather = fetchWeatherFromExternalApi(location);
            if (weather != null) {
                // Save to database
                weather = weatherRepository.save(weather);
                return new WeatherResponse(weather);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch weather from external API for location: {}", location, e);
        }
        
        // Fallback to database
        Optional<Weather> cachedWeather = weatherRepository.findTopByLocationIgnoreCaseOrderByTimestampDesc(location);
        if (cachedWeather.isPresent()) {
            logger.info("Returning cached weather data for location: {}", location);
            return new WeatherResponse(cachedWeather.get());
        }
        
        // If no data available, return mock data
        logger.info("No weather data available, returning mock data for location: {}", location);
        return createMockWeatherResponse(location);
    }
    
    /**
     * Get weather history for a location
     */
    public List<WeatherResponse> getWeatherHistory(String location, int limit) {
        logger.info("Getting weather history for location: {} with limit: {}", location, limit);
        
        List<Weather> weatherHistory = weatherRepository.findByLocationIgnoreCaseOrderByTimestampDesc(location);
        
        return weatherHistory.stream()
                .limit(limit)
                .map(WeatherResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get weather by coordinates
     */
    public WeatherResponse getWeatherByCoordinates(double latitude, double longitude) {
        logger.info("Getting weather for coordinates: {}, {}", latitude, longitude);
        
        try {
            Weather weather = fetchWeatherFromExternalApiByCoordinates(latitude, longitude);
            if (weather != null) {
                weather = weatherRepository.save(weather);
                return new WeatherResponse(weather);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch weather from external API for coordinates: {}, {}", latitude, longitude, e);
        }
        
        // Return mock data if external API fails
        return createMockWeatherResponseByCoordinates(latitude, longitude);
    }
    
    /**
     * Save weather data
     */
    public WeatherResponse saveWeather(WeatherRequest request) {
        logger.info("Saving weather data for location: {}", request.getLocation());
        
        Weather weather = new Weather();
        weather.setLocation(request.getLocation());
        weather.setCountry(request.getCountry());
        weather.setLatitude(request.getLatitude());
        weather.setLongitude(request.getLongitude());
        weather.setTimestamp(LocalDateTime.now());
        
        // Set some default values if not provided
        weather.setTemperature(20.0); // Default temperature
        weather.setDescription("Clear sky");
        
        weather = weatherRepository.save(weather);
        return new WeatherResponse(weather);
    }
    
    /**
     * Get all weather records
     */
    public List<WeatherResponse> getAllWeather() {
        logger.info("Getting all weather records");
        
        List<Weather> allWeather = weatherRepository.findAll();
        return allWeather.stream()
                .map(WeatherResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete weather record by ID
     */
    public boolean deleteWeather(Long id) {
        logger.info("Deleting weather record with ID: {}", id);
        
        if (weatherRepository.existsById(id)) {
            weatherRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Fetch weather from external API (OpenWeatherMap)
     */
    private Weather fetchWeatherFromExternalApi(String location) {
        try {
            WebClient webClient = webClientBuilder.build();
            
            String url = String.format("%s/weather?q=%s&appid=%s&units=metric", 
                    apiUrl, location, apiKey);
            
            // This is a simplified implementation
            // In a real application, you would parse the actual API response
            logger.info("Would fetch weather from: {}", url);
            
            // For demo purposes, return null to trigger fallback
            return null;
            
        } catch (Exception e) {
            logger.error("Error fetching weather from external API", e);
            return null;
        }
    }
    
    /**
     * Fetch weather from external API by coordinates
     */
    private Weather fetchWeatherFromExternalApiByCoordinates(double latitude, double longitude) {
        try {
            WebClient webClient = webClientBuilder.build();
            
            String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric", 
                    apiUrl, latitude, longitude, apiKey);
            
            logger.info("Would fetch weather from: {}", url);
            
            // For demo purposes, return null to trigger fallback
            return null;
            
        } catch (Exception e) {
            logger.error("Error fetching weather from external API by coordinates", e);
            return null;
        }
    }
    
    /**
     * Create mock weather response for demo purposes
     */
    private WeatherResponse createMockWeatherResponse(String location) {
        Weather weather = new Weather();
        weather.setLocation(location);
        weather.setTemperature(22.5);
        weather.setHumidity(65.0);
        weather.setPressure(1013.25);
        weather.setWindSpeed(5.2);
        weather.setWindDirection("NW");
        weather.setDescription("Partly cloudy");
        weather.setIcon("02d");
        weather.setTimestamp(LocalDateTime.now());
        weather.setCountry("Unknown");
        
        return new WeatherResponse(weather);
    }
    
    /**
     * Create mock weather response by coordinates
     */
    private WeatherResponse createMockWeatherResponseByCoordinates(double latitude, double longitude) {
        Weather weather = new Weather();
        weather.setLocation("Unknown Location");
        weather.setLatitude(latitude);
        weather.setLongitude(longitude);
        weather.setTemperature(20.0);
        weather.setHumidity(60.0);
        weather.setPressure(1015.0);
        weather.setWindSpeed(3.5);
        weather.setWindDirection("E");
        weather.setDescription("Clear sky");
        weather.setIcon("01d");
        weather.setTimestamp(LocalDateTime.now());
        
        return new WeatherResponse(weather);
    }
}
