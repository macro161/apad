package com.apad.weather.exception;

public class WeatherNotFoundException extends WeatherException {

    public WeatherNotFoundException(String message) {
        super(message);
    }

    public static WeatherNotFoundException forLocation(String location) {
        return new WeatherNotFoundException("Weather data not found for location: " + location);
    }
}
