package com.apad.weather.controller;

import com.apad.weather.dto.ApiResponse;
import com.apad.weather.dto.WeatherRequest;
import com.apad.weather.dto.WeatherResponse;
import com.apad.weather.service.WeatherService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    @Autowired
    private WeatherService weatherService;
    
    /**
     * Get current weather by location name
     * GET /api/weather/current?location=London
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<WeatherResponse>> getCurrentWeather(
            @RequestParam String location) {
        
        logger.info("Received request for current weather in location: {}", location);
        
        try {
            WeatherResponse weather = weatherService.getCurrentWeather(location);
            return ResponseEntity.ok(ApiResponse.success("Weather data retrieved successfully", weather));
        } catch (Exception e) {
            logger.error("Error getting current weather for location: {}", location, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve weather data: " + e.getMessage()));
        }
    }
    
    /**
     * Get current weather by coordinates
     * GET /api/weather/coordinates?lat=51.5074&lon=-0.1278
     */
    @GetMapping("/coordinates")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeatherByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon) {
        
        logger.info("Received request for weather at coordinates: {}, {}", lat, lon);
        
        try {
            WeatherResponse weather = weatherService.getWeatherByCoordinates(lat, lon);
            return ResponseEntity.ok(ApiResponse.success("Weather data retrieved successfully", weather));
        } catch (Exception e) {
            logger.error("Error getting weather for coordinates: {}, {}", lat, lon, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve weather data: " + e.getMessage()));
        }
    }
    
    /**
     * Get weather history for a location
     * GET /api/weather/history?location=London&limit=10
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<WeatherResponse>>> getWeatherHistory(
            @RequestParam String location,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Received request for weather history in location: {} with limit: {}", location, limit);
        
        try {
            List<WeatherResponse> history = weatherService.getWeatherHistory(location, limit);
            return ResponseEntity.ok(ApiResponse.success("Weather history retrieved successfully", history));
        } catch (Exception e) {
            logger.error("Error getting weather history for location: {}", location, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve weather history: " + e.getMessage()));
        }
    }
    
    /**
     * Get all weather records
     * GET /api/weather/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WeatherResponse>>> getAllWeather() {
        
        logger.info("Received request for all weather records");
        
        try {
            List<WeatherResponse> allWeather = weatherService.getAllWeather();
            return ResponseEntity.ok(ApiResponse.success("All weather data retrieved successfully", allWeather));
        } catch (Exception e) {
            logger.error("Error getting all weather records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve weather data: " + e.getMessage()));
        }
    }
    
    /**
     * Save weather data
     * POST /api/weather
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WeatherResponse>> saveWeather(
            @Valid @RequestBody WeatherRequest request) {
        
        logger.info("Received request to save weather data for location: {}", request.getLocation());
        
        try {
            WeatherResponse weather = weatherService.saveWeather(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Weather data saved successfully", weather));
        } catch (Exception e) {
            logger.error("Error saving weather data for location: {}", request.getLocation(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to save weather data: " + e.getMessage()));
        }
    }
    
    /**
     * Delete weather record by ID
     * DELETE /api/weather/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWeather(@PathVariable Long id) {
        
        logger.info("Received request to delete weather record with ID: {}", id);
        
        try {
            boolean deleted = weatherService.deleteWeather(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Weather record deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Weather record not found with ID: " + id));
            }
        } catch (Exception e) {
            logger.error("Error deleting weather record with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete weather record: " + e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/weather/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Weather API is running", "OK"));
    }
}
