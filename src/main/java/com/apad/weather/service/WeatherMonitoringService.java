package com.apad.weather.service;

import com.apad.weather.model.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherMonitoringService.class);

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private SlackNotificationService slackNotificationService;

    // Store previous weather data to detect changes
    private final Map<String, Weather> previousWeatherData = new HashMap<>();

    // Cities to monitor for alerts
    private final List<String> monitoredCities = Arrays.asList(
            "London", "New York", "Tokyo", "Paris", "Sydney", "Moscow", "Mumbai", "Berlin"
    );

    /**
     * Check for extreme weather conditions every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void checkForExtremeWeather() {
        logger.info("Checking for extreme weather conditions...");
        
        for (String city : monitoredCities) {
            try {
                Weather currentWeather = weatherService.getWeatherByCity(city);
                
                // Check for extreme weather conditions
                slackNotificationService.sendExtremeWeatherAlert(currentWeather);
                
                // Check for significant weather changes
                Weather previousWeather = previousWeatherData.get(city.toLowerCase());
                if (previousWeather != null) {
                    slackNotificationService.sendWeatherChangeAlert(previousWeather, currentWeather);
                }
                
                // Store current weather for next comparison
                previousWeatherData.put(city.toLowerCase(), currentWeather);
                
            } catch (Exception e) {
                logger.error("Failed to check weather for city: {}", city, e);
            }
        }
    }

    /**
     * Send daily weather summary at 8 AM
     */
    @Scheduled(cron = "0 0 8 * * *") // Daily at 8:00 AM
    public void sendDailyWeatherSummary() {
        logger.info("Sending daily weather summary...");
        
        for (String city : monitoredCities) {
            try {
                Weather weather = weatherService.getWeatherByCity(city);
                slackNotificationService.sendDailyWeatherSummary(weather);
            } catch (Exception e) {
                logger.error("Failed to send daily summary for city: {}", city, e);
            }
        }
    }

    /**
     * Manual trigger for testing alerts
     */
    public void triggerTestAlert(String city) {
        logger.info("Triggering test alert for city: {}", city);
        
        try {
            Weather weather = weatherService.getWeatherByCity(city);
            String testMessage = String.format("🧪 *Test Alert* 🧪\n" +
                    "Weather monitoring system is working correctly!\n" +
                    "Current conditions in %s: %s, %d°C",
                    weather.getCity(),
                    weather.getDescription(),
                    weather.getTemperatureCelsius());
            
            slackNotificationService.sendWeatherAlert(weather, "TEST", testMessage);
        } catch (Exception e) {
            logger.error("Failed to send test alert for city: {}", city, e);
        }
    }

    /**
     * Check specific weather conditions that require immediate alerts
     */
    public void checkWeatherCondition(Weather weather) {
        int tempC = weather.getTemperatureCelsius();
        double windSpeed = weather.getWindSpeed();
        String description = weather.getDescription().toLowerCase();

        // Immediate alert conditions
        if (tempC <= -25) {
            String message = String.format("🚨 *CRITICAL COLD WARNING* 🚨\n" +
                    "Extremely dangerous cold conditions in %s: %d°C (%d°F)\n" +
                    "Risk of frostbite within minutes. Avoid outdoor exposure!",
                    weather.getCity(), tempC, weather.getTemperatureFahrenheit());
            slackNotificationService.sendWeatherAlert(weather, "CRITICAL_COLD", message);
        }

        if (tempC >= 45) {
            String message = String.format("🚨 *CRITICAL HEAT WARNING* 🚨\n" +
                    "Extremely dangerous heat conditions in %s: %d°C (%d°F)\n" +
                    "Risk of heat stroke. Stay indoors and hydrated!",
                    weather.getCity(), tempC, weather.getTemperatureFahrenheit());
            slackNotificationService.sendWeatherAlert(weather, "CRITICAL_HEAT", message);
        }

        if (windSpeed >= 25) {
            String message = String.format("💨 *SEVERE WIND WARNING* 💨\n" +
                    "Dangerous wind conditions in %s: %.1f m/s (%.1f mph)\n" +
                    "Avoid outdoor activities. Secure loose objects!",
                    weather.getCity(), windSpeed, windSpeed * 2.237);
            slackNotificationService.sendWeatherAlert(weather, "SEVERE_WIND", message);
        }

        if (description.contains("storm") || description.contains("thunderstorm")) {
            String message = String.format("⛈️ *STORM ALERT* ⛈️\n" +
                    "Severe weather conditions in %s: %s\n" +
                    "Stay indoors and avoid travel if possible!",
                    weather.getCity(), weather.getDescription());
            slackNotificationService.sendWeatherAlert(weather, "STORM_ALERT", message);
        }
    }

    /**
     * Get monitoring status
     */
    public Map<String, Object> getMonitoringStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("monitoredCities", monitoredCities);
        status.put("lastCheckTime", System.currentTimeMillis());
        status.put("trackedCities", previousWeatherData.keySet());
        return status;
    }
}