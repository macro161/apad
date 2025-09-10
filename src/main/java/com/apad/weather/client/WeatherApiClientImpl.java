package com.apad.weather.client;

import com.apad.weather.config.WeatherApiProperties;
import com.apad.weather.domain.Location;
import com.apad.weather.domain.WeatherCondition;
import com.apad.weather.domain.WeatherData;
import com.apad.weather.exception.WeatherServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WeatherAPI.com client implementation using WebClient.
 * Integrates with WeatherAPI.com service using Java 21 features and reactive programming.
 */
@Component
public class WeatherApiClientImpl implements WeatherApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiClientImpl.class);
    private static final String CURRENT_WEATHER_ENDPOINT = "/current.json";
    private static final String HEALTH_CHECK_ENDPOINT = "/current.json";
    
    private final WebClient webClient;
    private final WeatherApiProperties properties;
    
    public WeatherApiClientImpl(WebClient.Builder webClientBuilder, WeatherApiProperties properties) {
        this.properties = properties;
        this.webClient = webClientBuilder
            .baseUrl(properties.getBaseUrl())
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
    
    @Override
    public Mono<WeatherData> getCurrentWeather(String query) {
        logger.debug("Fetching weather data for query: {}", query);
        
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(CURRENT_WEATHER_ENDPOINT)
                .queryParam("key", properties.getApiKey())
                .queryParam("q", query)
                .queryParam("aqi", "no")
                .build())
            .retrieve()
            .onStatus(HttpStatus.UNAUTHORIZED::equals, 
                response -> Mono.error(WeatherServiceException.invalidApiKey()))
            .onStatus(HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(String.class)
                    .map(body -> WeatherServiceException.locationNotFound(query)))
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals,
                response -> Mono.error(WeatherServiceException.rateLimitExceeded()))
            .onStatus(status -> status.is5xxServerError(),
                response -> Mono.error(WeatherServiceException.serviceUnavailable()))
            .bodyToMono(WeatherApiResponse.class)
            .map(this::mapToWeatherData)
            .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
            .onErrorMap(this::mapException)
            .doOnSuccess(data -> logger.debug("Successfully fetched weather data for: {}", query))
            .doOnError(error -> logger.error("Failed to fetch weather data for: {}", query, error));
    }
    
    @Override
    public Mono<String> healthCheck() {
        logger.debug("Performing health check");
        
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(HEALTH_CHECK_ENDPOINT)
                .queryParam("key", properties.getApiKey())
                .queryParam("q", "London")
                .queryParam("aqi", "no")
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(5))
            .map(response -> "OK")
            .onErrorReturn("UNAVAILABLE")
            .doOnSuccess(status -> logger.debug("Health check result: {}", status));
    }
    
    /**
     * Map WeatherAPI response to domain model using Java 21 features
     */
    private WeatherData mapToWeatherData(WeatherApiResponse response) {
        var location = new Location(
            response.location().name(),
            response.location().country(),
            response.location().region(),
            response.location().lat(),
            response.location().lon(),
            response.location().tzId()
        );
        
        var condition = new WeatherCondition(
            response.current().condition().text(),
            response.current().condition().icon(),
            response.current().condition().code()
        );
        
        return new WeatherData(
            location,
            condition,
            response.current().tempC(),
            response.current().tempF(),
            response.current().feelslikeC(),
            response.current().feelslikeF(),
            response.current().humidity(),
            response.current().windKph(),
            response.current().windMph(),
            response.current().windDegree(),
            response.current().windDir(),
            response.current().pressureMb(),
            response.current().pressureIn(),
            response.current().precipMm(),
            response.current().precipIn(),
            response.current().visKm(),
            response.current().visMiles(),
            response.current().uv(),
            parseLastUpdated(response.current().lastUpdated())
        );
    }
    
    /**
     * Parse last updated timestamp
     */
    private LocalDateTime parseLastUpdated(String lastUpdated) {
        try {
            return LocalDateTime.parse(lastUpdated, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            logger.warn("Failed to parse last updated timestamp: {}", lastUpdated, e);
            return LocalDateTime.now();
        }
    }
    
    /**
     * Map various exceptions to WeatherServiceException using Java 21 pattern matching
     */
    private WeatherServiceException mapException(Throwable error) {
        return switch (error) {
            case WeatherServiceException wse -> wse;
            case WebClientResponseException wcre -> {
                logger.error("WebClient response error: {} - {}", wcre.getStatusCode(), wcre.getResponseBodyAsString());
                yield switch (wcre.getStatusCode().value()) {
                    case 400 -> WeatherServiceException.locationNotFound("Invalid location");
                    case 401 -> WeatherServiceException.invalidApiKey();
                    case 429 -> WeatherServiceException.rateLimitExceeded();
                    default -> new WeatherServiceException("Weather API error: " + wcre.getMessage(), wcre, true);
                };
            }
            case java.util.concurrent.TimeoutException te -> WeatherServiceException.timeout();
            default -> new WeatherServiceException("Unexpected error: " + error.getMessage(), error, false);
        };
    }
    
    /**
     * WeatherAPI.com response DTOs using Java 21 records
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherApiResponse(
        LocationResponse location,
        CurrentResponse current
    ) {}
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocationResponse(
        String name,
        String region,
        String country,
        Double lat,
        Double lon,
        @JsonProperty("tz_id") String tzId
    ) {}
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentResponse(
        @JsonProperty("last_updated") String lastUpdated,
        @JsonProperty("temp_c") Double tempC,
        @JsonProperty("temp_f") Double tempF,
        @JsonProperty("feelslike_c") Double feelslikeC,
        @JsonProperty("feelslike_f") Double feelslikeF,
        Integer humidity,
        @JsonProperty("wind_kph") Double windKph,
        @JsonProperty("wind_mph") Double windMph,
        @JsonProperty("wind_degree") Integer windDegree,
        @JsonProperty("wind_dir") String windDir,
        @JsonProperty("pressure_mb") Double pressureMb,
        @JsonProperty("pressure_in") Double pressureIn,
        @JsonProperty("precip_mm") Double precipMm,
        @JsonProperty("precip_in") Double precipIn,
        @JsonProperty("vis_km") Double visKm,
        @JsonProperty("vis_miles") Double visMiles,
        Double uv,
        ConditionResponse condition
    ) {}
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ConditionResponse(
        String text,
        String icon,
        Integer code
    ) {}
}