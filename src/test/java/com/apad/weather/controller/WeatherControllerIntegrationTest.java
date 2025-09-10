package com.apad.weather.controller;

import com.apad.weather.client.WeatherApiClient;
import com.apad.weather.domain.Location;
import com.apad.weather.domain.WeatherCondition;
import com.apad.weather.domain.WeatherData;
import com.apad.weather.dto.WeatherRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for WeatherController using WebTestClient.
 * Tests the complete request/response flow with mocked external dependencies.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WeatherControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private WeatherApiClient weatherApiClient;
    
    @Test
    void getCurrentWeather_WithValidRequest_ShouldReturnWeatherData() throws Exception {
        // Given
        var mockWeatherData = createMockWeatherData();
        when(weatherApiClient.getCurrentWeather(anyString()))
            .thenReturn(Mono.just(mockWeatherData));
        
        var request = WeatherRequest.forLocation("London");
        
        // When & Then
        webTestClient.post()
            .uri("/api/weather/current")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.location.name").isEqualTo("London")
            .jsonPath("$.condition.text").isEqualTo("Partly cloudy")
            .jsonPath("$.temperature").isEqualTo(22.5)
            .jsonPath("$.temperatureUnit").isEqualTo("celsius")
            .jsonPath("$.humidity").isEqualTo(65);
    }
    
    @Test
    void getCurrentWeather_WithInvalidRequest_ShouldReturnValidationError() {
        // Given - request with no location or coordinates
        var invalidRequest = new WeatherRequest(null, null, null, "celsius", "kph");
        
        // When & Then
        webTestClient.post()
            .uri("/api/weather/current")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            .jsonPath("$.message").isEqualTo("Request validation failed");
    }
    
    @Test
    void getCurrentWeatherByLocation_WithValidLocation_ShouldReturnWeatherData() {
        // Given
        var mockWeatherData = createMockWeatherData();
        when(weatherApiClient.getCurrentWeather(anyString()))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        webTestClient.get()
            .uri("/api/weather/current?location=London&temperatureUnit=celsius&windSpeedUnit=kph")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.location.name").isEqualTo("London")
            .jsonPath("$.temperature").isEqualTo(22.5)
            .jsonPath("$.temperatureUnit").isEqualTo("celsius");
    }
    
    @Test
    void getCurrentWeatherByLocation_WithBlankLocation_ShouldReturnValidationError() {
        // When & Then
        webTestClient.get()
            .uri("/api/weather/current?location=")
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo("VALIDATION_ERROR");
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithValidCoordinates_ShouldReturnWeatherData() {
        // Given
        var mockWeatherData = createMockWeatherData();
        when(weatherApiClient.getCurrentWeather(anyString()))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        webTestClient.get()
            .uri("/api/weather/current/coordinates?latitude=51.5074&longitude=-0.1278")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.location.name").isEqualTo("London")
            .jsonPath("$.temperature").isEqualTo(22.5);
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithInvalidLatitude_ShouldReturnValidationError() {
        // When & Then
        webTestClient.get()
            .uri("/api/weather/current/coordinates?latitude=95.0&longitude=-0.1278")
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            .jsonPath("$.validationErrors[0].field").isEqualTo("latitude")
            .jsonPath("$.validationErrors[0].message").value(
                org.hamcrest.Matchers.containsString("between -90 and 90"));
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithInvalidLongitude_ShouldReturnValidationError() {
        // When & Then
        webTestClient.get()
            .uri("/api/weather/current/coordinates?latitude=51.5074&longitude=185.0")
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
            .jsonPath("$.validationErrors[0].field").isEqualTo("longitude")
            .jsonPath("$.validationErrors[0].message").value(
                org.hamcrest.Matchers.containsString("between -180 and 180"));
    }
    
    @Test
    void healthCheck_WhenServiceAvailable_ShouldReturnOk() {
        // Given
        when(weatherApiClient.healthCheck())
            .thenReturn(Mono.just("OK"));
        
        // When & Then
        webTestClient.get()
            .uri("/api/weather/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("Weather service is available");
    }
    
    @Test
    void healthCheck_WhenServiceUnavailable_ShouldReturnServiceUnavailable() {
        // Given
        when(weatherApiClient.healthCheck())
            .thenReturn(Mono.just("UNAVAILABLE"));
        
        // When & Then
        webTestClient.get()
            .uri("/api/weather/health")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class)
            .isEqualTo("Weather service is unavailable");
    }
    
    /**
     * Create mock weather data for testing
     */
    private WeatherData createMockWeatherData() {
        var location = new Location(
            "London",
            "United Kingdom",
            "City of London, Greater London",
            51.5074,
            -0.1278,
            "Europe/London"
        );
        
        var condition = new WeatherCondition(
            "Partly cloudy",
            "//cdn.weatherapi.com/weather/64x64/day/116.png",
            1003
        );
        
        return new WeatherData(
            location,
            condition,
            22.5,
            72.5,
            24.1,
            75.4,
            65,
            15.2,
            9.4,
            270,
            "W",
            1013.2,
            29.91,
            0.0,
            0.0,
            10.0,
            6.2,
            5.2,
            LocalDateTime.now()
        );
    }
}