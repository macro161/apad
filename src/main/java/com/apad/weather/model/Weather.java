package com.apad.weather.model;

public class Weather {
    private String city;
    private String country;
    private double temperature;
    private String description;
    private double humidity;
    private double windSpeed;
    private String icon;
    private long timestamp;

    public Weather() {}

    public Weather(String city, String country, double temperature, String description, 
                   double humidity, double windSpeed, String icon) {
        this.city = city;
        this.country = country;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.icon = icon;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTemperatureCelsius() {
        return (int) Math.round(temperature - 273.15);
    }

    public int getTemperatureFahrenheit() {
        return (int) Math.round((temperature - 273.15) * 9/5 + 32);
    }
}