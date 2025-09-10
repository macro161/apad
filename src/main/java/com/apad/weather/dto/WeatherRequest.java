package com.apad.weather.dto;

import javax.validation.constraints.NotBlank;

public class WeatherRequest {
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String country;
    private Double latitude;
    private Double longitude;
    
    // Constructors
    public WeatherRequest() {}
    
    public WeatherRequest(String location) {
        this.location = location;
    }
    
    public WeatherRequest(String location, String country) {
        this.location = location;
        this.country = country;
    }
    
    public WeatherRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
