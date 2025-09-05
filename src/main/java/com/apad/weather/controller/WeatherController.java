package com.apad.weather.controller;

import com.apad.weather.model.Weather;
import com.apad.weather.service.WeatherService;
import com.apad.weather.service.WeatherMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherMonitoringService weatherMonitoringService;

    @GetMapping("/")
    public String index(Model model) {
        Weather defaultWeather = weatherService.getCurrentWeather();
        model.addAttribute("weather", defaultWeather);
        return "index";
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam(required = false) String city, Model model) {
        Weather weather;
        if (city != null && !city.trim().isEmpty()) {
            weather = weatherService.getWeatherByCity(city);
        } else {
            weather = weatherService.getCurrentWeather();
        }
        model.addAttribute("weather", weather);
        model.addAttribute("searchCity", city);
        return "index";
    }

    @GetMapping("/api/weather")
    @ResponseBody
    public Weather getWeatherApi(@RequestParam(required = false) String city) {
        if (city != null && !city.trim().isEmpty()) {
            return weatherService.getWeatherByCity(city);
        }
        return weatherService.getCurrentWeather();
    }

    @GetMapping("/api/weather/{city}")
    @ResponseBody
    public Weather getWeatherByCity(@PathVariable String city) {
        Weather weather = weatherService.getWeatherByCity(city);
        
        // Check for conditions that require immediate alerts
        weatherMonitoringService.checkWeatherCondition(weather);
        
        return weather;
    }

    // Slack Alert Endpoints
    
    @PostMapping("/api/alerts/test/{city}")
    @ResponseBody
    public ResponseEntity<String> triggerTestAlert(@PathVariable String city) {
        try {
            weatherMonitoringService.triggerTestAlert(city);
            return ResponseEntity.ok("Test alert sent for " + city);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test alert: " + e.getMessage());
        }
    }

    @GetMapping("/api/monitoring/status")
    @ResponseBody
    public Map<String, Object> getMonitoringStatus() {
        return weatherMonitoringService.getMonitoringStatus();
    }

    @PostMapping("/api/monitoring/check")
    @ResponseBody
    public ResponseEntity<String> triggerManualCheck() {
        try {
            weatherMonitoringService.checkForExtremeWeather();
            return ResponseEntity.ok("Manual weather check completed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to perform manual check: " + e.getMessage());
        }
    }

    @PostMapping("/api/alerts/summary")
    @ResponseBody
    public ResponseEntity<String> triggerDailySummary() {
        try {
            weatherMonitoringService.sendDailyWeatherSummary();
            return ResponseEntity.ok("Daily weather summary sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send daily summary: " + e.getMessage());
        }
    }
}