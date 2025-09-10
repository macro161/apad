package com.apad.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Main configuration class for the weather application.
 * Configures caching, WebClient, and OpenAPI documentation.
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(WeatherApiProperties.class)
public class WeatherConfiguration {
    
    /**
     * Configure WebClient.Builder with common settings
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .codecs(configurer -> {
                configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
                configurer.defaultCodecs().enableLoggingRequestDetails(true);
            });
    }
    
    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI weatherOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Weather API")
                .description("A comprehensive weather application backend built with Spring Boot 3 and Java 21")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Weather API Team")
                    .email("support@weather-api.com")
                    .url("https://weather-api.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
}