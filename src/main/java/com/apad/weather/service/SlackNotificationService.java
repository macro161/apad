package com.apad.weather.service;

import com.apad.weather.model.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SlackNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SlackNotificationService.class);

    @Value("${slack.webhook.url:}")
    private String slackWebhookUrl;

    @Value("${slack.notifications.enabled:false}")
    private boolean notificationsEnabled;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public SlackNotificationService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void sendWeatherAlert(Weather weather, String alertType, String message) {
        if (!notificationsEnabled || slackWebhookUrl == null || slackWebhookUrl.isEmpty()) {
            logger.info("Slack notifications disabled or webhook URL not configured");
            return;
        }

        try {
            String slackMessage = buildSlackMessage(weather, alertType, message);
            sendSlackMessage(slackMessage);
        } catch (Exception e) {
            logger.error("Failed to send Slack notification", e);
        }
    }

    public void sendExtremeWeatherAlert(Weather weather) {
        String alertType = determineAlertType(weather);
        if (alertType != null) {
            String message = buildExtremeWeatherMessage(weather, alertType);
            sendWeatherAlert(weather, alertType, message);
        }
    }

    public void sendDailyWeatherSummary(Weather weather) {
        String message = String.format("Daily weather summary for %s: %s, %d°C (%d°F)",
                weather.getCity(),
                weather.getDescription(),
                weather.getTemperatureCelsius(),
                weather.getTemperatureFahrenheit());
        sendWeatherAlert(weather, "DAILY_SUMMARY", message);
    }

    public void sendWeatherChangeAlert(Weather oldWeather, Weather newWeather) {
        int tempDiff = Math.abs(oldWeather.getTemperatureCelsius() - newWeather.getTemperatureCelsius());
        if (tempDiff >= 10) { // Alert if temperature changed by 10°C or more
            String message = String.format("Significant temperature change in %s: %d°C → %d°C (Δ%+d°C)",
                    newWeather.getCity(),
                    oldWeather.getTemperatureCelsius(),
                    newWeather.getTemperatureCelsius(),
                    newWeather.getTemperatureCelsius() - oldWeather.getTemperatureCelsius());
            sendWeatherAlert(newWeather, "TEMPERATURE_CHANGE", message);
        }
    }

    private String determineAlertType(Weather weather) {
        int tempC = weather.getTemperatureCelsius();
        double windSpeed = weather.getWindSpeed();
        String description = weather.getDescription().toLowerCase();

        // Extreme temperature alerts
        if (tempC <= -20) return "EXTREME_COLD";
        if (tempC >= 40) return "EXTREME_HEAT";
        if (tempC <= 0 && description.contains("snow")) return "SNOW_ALERT";

        // Wind alerts
        if (windSpeed >= 20) return "HIGH_WIND";

        // Weather condition alerts
        if (description.contains("heavy rain") || description.contains("storm")) return "SEVERE_WEATHER";
        if (description.contains("snow") && tempC > -5) return "SNOW_WARNING";

        return null; // No alert needed
    }

    private String buildExtremeWeatherMessage(Weather weather, String alertType) {
        String emoji = getAlertEmoji(alertType);
        String severity = getAlertSeverity(alertType);
        
        return String.format("%s *%s Weather Alert for %s* %s\n" +
                        "• Temperature: %d°C (%d°F)\n" +
                        "• Conditions: %s\n" +
                        "• Wind Speed: %.1f m/s\n" +
                        "• Humidity: %.0f%%\n" +
                        "Please take appropriate precautions!",
                emoji, severity, weather.getCity(), emoji,
                weather.getTemperatureCelsius(),
                weather.getTemperatureFahrenheit(),
                weather.getDescription(),
                weather.getWindSpeed(),
                weather.getHumidity());
    }

    private String buildSlackMessage(Weather weather, String alertType, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", message);
        payload.put("username", "Weather Bot");
        payload.put("icon_emoji", ":cloud:");

        // Add rich formatting for extreme weather alerts
        if (alertType.contains("EXTREME") || alertType.contains("SEVERE")) {
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("color", getAlertColor(alertType));
            attachment.put("title", String.format("Weather Alert: %s", weather.getCity()));
            attachment.put("text", message);
            attachment.put("footer", "Weather Monitoring System");
            attachment.put("ts", weather.getTimestamp() / 1000);

            payload.put("attachments", new Object[]{attachment});
        }

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            logger.error("Failed to serialize Slack message", e);
            return "{\"text\":\"" + message + "\"}";
        }
    }

    private void sendSlackMessage(String message) {
        webClient.post()
                .uri(slackWebhookUrl)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Slack notification sent successfully"))
                .doOnError(error -> logger.error("Failed to send Slack notification: {}", error.getMessage()))
                .onErrorResume(error -> Mono.empty())
                .subscribe();
    }

    private String getAlertEmoji(String alertType) {
        switch (alertType) {
            case "EXTREME_COLD": return "🥶";
            case "EXTREME_HEAT": return "🔥";
            case "SNOW_ALERT": case "SNOW_WARNING": return "❄️";
            case "HIGH_WIND": return "💨";
            case "SEVERE_WEATHER": return "⛈️";
            case "TEMPERATURE_CHANGE": return "🌡️";
            case "DAILY_SUMMARY": return "🌤️";
            default: return "⚠️";
        }
    }

    private String getAlertSeverity(String alertType) {
        switch (alertType) {
            case "EXTREME_COLD": case "EXTREME_HEAT": case "SEVERE_WEATHER": return "EXTREME";
            case "HIGH_WIND": case "SNOW_ALERT": return "HIGH";
            case "SNOW_WARNING": case "TEMPERATURE_CHANGE": return "MODERATE";
            default: return "INFO";
        }
    }

    private String getAlertColor(String alertType) {
        switch (alertType) {
            case "EXTREME_COLD": case "EXTREME_HEAT": case "SEVERE_WEATHER": return "danger";
            case "HIGH_WIND": case "SNOW_ALERT": return "warning";
            case "SNOW_WARNING": case "TEMPERATURE_CHANGE": return "good";
            default: return "#36a64f";
        }
    }
}