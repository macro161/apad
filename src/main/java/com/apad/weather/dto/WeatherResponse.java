package com.apad.weather.dto;

import com.apad.weather.model.Weather;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class WeatherResponse {
    
    private Long id;
    private String location;
    private Double temperature;
    private Double humidity;
    private Double pressure;
    private Double windSpeed;
    private String windDirection;
    private String description;
    private String icon;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String country;
    private Double latitude;
    private Double longitude;
    
    // Constructors
    public WeatherResponse() {}
    
    public WeatherResponse(Weather weather) {
        this.id = weather.getId();
        this.location = weather.getLocation();
        this.temperature = weather.getTemperature();
        this.humidity = weather.getHumidity();
        this.pressure = weather.getPressure();
        this.windSpeed = weather.getWindSpeed();
        this.windDirection = weather.getWindDirection();
        this.description = weather.getDescription();
        this.icon = weather.getIcon();
        this.timestamp = weather.getTimestamp();
        this.country = weather.getCountry();
        this.latitude = weather.getLatitude();
        this.longitude = weather.getLongitude();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Double getHumidity() {
        return humidity;
    }
    
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }
    
    public Double getPressure() {
        return pressure;
    }
    
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }
    
    public Double getWindSpeed() {
        return windSpeed;
    }
    
    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }
    
    public String getWindDirection() {
        return windDirection;
    }
    
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
