package com.apad.weather.service;

import com.apad.weather.client.WeatherApiClient;
import com.apad.weather.domain.Location;
import com.apad.weather.domain.WeatherCondition;
import com.apad.weather.domain.WeatherData;
import com.apad.weather.dto.WeatherRequest;
import com.apad.weather.exception.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for WeatherServiceImpl using reactive testing.
 * Tests service logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceImplTest {
    
    @Mock
    private WeatherApiClient weatherApiClient;
    
    private WeatherServiceImpl weatherService;
    
    @BeforeEach
    void setUp() {
        weatherService = new WeatherServiceImpl(weatherApiClient);
    }
    
    @Test
    void getCurrentWeather_WithValidRequest_ShouldReturnWeatherData() {
        // Given
        var request = WeatherRequest.forLocation("London");
        var mockWeatherData = createMockWeatherData();
        
        when(weatherApiClient.getCurrentWeather("London"))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeather(request))
            .assertNext(weatherData -> {
                assertThat(weatherData.location().name()).isEqualTo("London");
                assertThat(weatherData.condition().text()).isEqualTo("Partly cloudy");
                assertThat(weatherData.temperatureCelsius()).isEqualTo(22.5);
                assertThat(weatherData.humidity()).isEqualTo(65);
            })
            .verifyComplete();
    }
    
    @Test
    void getCurrentWeather_WithCoordinatesRequest_ShouldReturnWeatherData() {
        // Given
        var request = WeatherRequest.forCoordinates(51.5074, -0.1278);
        var mockWeatherData = createMockWeatherData();
        
        when(weatherApiClient.getCurrentWeather("51.5074,-0.1278"))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeather(request))
            .assertNext(weatherData -> {
                assertThat(weatherData.location().latitude()).isEqualTo(51.5074);
                assertThat(weatherData.location().longitude()).isEqualTo(-0.1278);
            })
            .verifyComplete();
    }
    
    @Test
    void getCurrentWeather_WhenApiClientFails_ShouldReturnError() {
        // Given
        var request = WeatherRequest.forLocation("InvalidLocation");
        
        when(weatherApiClient.getCurrentWeather("InvalidLocation"))
            .thenReturn(Mono.error(new RuntimeException("API Error")));
        
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeather(request))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByLocation_WithValidLocation_ShouldReturnWeatherData() {
        // Given
        var mockWeatherData = createMockWeatherData();
        
        when(weatherApiClient.getCurrentWeather("Paris"))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByLocation("Paris"))
            .assertNext(weatherData -> {
                assertThat(weatherData).isNotNull();
                assertThat(weatherData.location().name()).isEqualTo("London");
            })
            .verifyComplete();
    }
    
    @Test
    void getCurrentWeatherByLocation_WithNullLocation_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByLocation(null))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByLocation_WithBlankLocation_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByLocation("   "))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithValidCoordinates_ShouldReturnWeatherData() {
        // Given
        var mockWeatherData = createMockWeatherData();
        
        when(weatherApiClient.getCurrentWeather(anyString()))
            .thenReturn(Mono.just(mockWeatherData));
        
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByCoordinates(51.5074, -0.1278))
            .assertNext(weatherData -> {
                assertThat(weatherData).isNotNull();
                assertThat(weatherData.location().latitude()).isEqualTo(51.5074);
            })
            .verifyComplete();
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithNullLatitude_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByCoordinates(null, -0.1278))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithNullLongitude_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByCoordinates(51.5074, null))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithInvalidLatitude_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByCoordinates(95.0, -0.1278))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void getCurrentWeatherByCoordinates_WithInvalidLongitude_ShouldReturnError() {
        // When & Then
        StepVerifier.create(weatherService.getCurrentWeatherByCoordinates(51.5074, 185.0))
            .expectError(WeatherServiceException.class)
            .verify();
    }
    
    @Test
    void isServiceAvailable_WhenHealthCheckSucceeds_ShouldReturnTrue() {
        // Given
        when(weatherApiClient.healthCheck())
            .thenReturn(Mono.just("OK"));
        
        // When & Then
        StepVerifier.create(weatherService.isServiceAvailable())
            .assertNext(available -> assertThat(available).isTrue())
            .verifyComplete();
    }
    
    @Test
    void isServiceAvailable_WhenHealthCheckFails_ShouldReturnFalse() {
        // Given
        when(weatherApiClient.healthCheck())
            .thenReturn(Mono.error(new RuntimeException("Service unavailable")));
        
        // When & Then
        StepVerifier.create(weatherService.isServiceAvailable())
            .assertNext(available -> assertThat(available).isFalse())
            .verifyComplete();
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