package com.apad.weather.controller;

import com.apad.weather.dto.ErrorResponse;
import com.apad.weather.dto.WeatherRequest;
import com.apad.weather.dto.WeatherResponse;
import com.apad.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Weather REST controller providing weather data endpoints.
 * Uses reactive programming and comprehensive validation.
 */
@RestController
@RequestMapping("/api/weather")
@Validated
@Tag(name = "Weather", description = "Weather data operations")
public class WeatherController {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    private final WeatherService weatherService;
    
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    /**
     * Get current weather by location name or coordinates
     */
    @PostMapping("/current")
    @Operation(summary = "Get current weather", 
               description = "Get current weather data for a location by name or coordinates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WeatherResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "502", description = "Weather service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ResponseEntity<WeatherResponse>> getCurrentWeather(
            @Valid @RequestBody WeatherRequest request) {
        
        logger.info("Received weather request: {}", request.getQueryString());
        
        return weatherService.getCurrentWeather(request)
            .map(weatherData -> WeatherResponse.from(weatherData, request))
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> logger.info("Successfully processed weather request for: {}", 
                request.getQueryString()))
            .doOnError(error -> logger.error("Failed to process weather request for: {}", 
                request.getQueryString(), error));
    }
    
    /**
     * Get current weather by location name
     */
    @GetMapping("/current")
    @Operation(summary = "Get current weather by location", 
               description = "Get current weather data for a location by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WeatherResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid location parameter",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ResponseEntity<WeatherResponse>> getCurrentWeatherByLocation(
            @Parameter(description = "Location name (city, address, etc.)", example = "London")
            @RequestParam @NotBlank(message = "Location cannot be blank") String location,
            
            @Parameter(description = "Temperature unit", example = "celsius")
            @RequestParam(defaultValue = "celsius") String temperatureUnit,
            
            @Parameter(description = "Wind speed unit", example = "kph")
            @RequestParam(defaultValue = "kph") String windSpeedUnit) {
        
        logger.info("Received weather request for location: {}", location);
        
        WeatherRequest request = new WeatherRequest(location, null, null, temperatureUnit, windSpeedUnit);
        
        return weatherService.getCurrentWeather(request)
            .map(weatherData -> WeatherResponse.from(weatherData, request))
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> logger.info("Successfully processed weather request for location: {}", location))
            .doOnError(error -> logger.error("Failed to process weather request for location: {}", location, error));
    }
    
    /**
     * Get current weather by coordinates
     */
    @GetMapping("/current/coordinates")
    @Operation(summary = "Get current weather by coordinates", 
               description = "Get current weather data for a location by latitude and longitude")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WeatherResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid coordinate parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ResponseEntity<WeatherResponse>> getCurrentWeatherByCoordinates(
            @Parameter(description = "Latitude coordinate", example = "51.5074")
            @RequestParam 
            @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90 degrees")
            @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90 degrees")
            Double latitude,
            
            @Parameter(description = "Longitude coordinate", example = "-0.1278")
            @RequestParam 
            @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180 degrees")
            @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180 degrees")
            Double longitude,
            
            @Parameter(description = "Temperature unit", example = "celsius")
            @RequestParam(defaultValue = "celsius") String temperatureUnit,
            
            @Parameter(description = "Wind speed unit", example = "kph")
            @RequestParam(defaultValue = "kph") String windSpeedUnit) {
        
        logger.info("Received weather request for coordinates: {}, {}", latitude, longitude);
        
        WeatherRequest request = new WeatherRequest(null, latitude, longitude, temperatureUnit, windSpeedUnit);
        
        return weatherService.getCurrentWeather(request)
            .map(weatherData -> WeatherResponse.from(weatherData, request))
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> logger.info("Successfully processed weather request for coordinates: {}, {}", 
                latitude, longitude))
            .doOnError(error -> logger.error("Failed to process weather request for coordinates: {}, {}", 
                latitude, longitude, error));
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the weather service is available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy"),
        @ApiResponse(responseCode = "503", description = "Service is unavailable")
    })
    public Mono<ResponseEntity<String>> healthCheck() {
        logger.debug("Health check requested");
        
        return weatherService.isServiceAvailable()
            .map(available -> available ? 
                ResponseEntity.ok("Weather service is available") :
                ResponseEntity.status(503).body("Weather service is unavailable"))
            .doOnSuccess(response -> logger.debug("Health check completed with status: {}", 
                response.getStatusCode()));
    }
}