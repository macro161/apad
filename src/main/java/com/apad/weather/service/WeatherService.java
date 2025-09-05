package com.apad.weather.service;

import com.apad.weather.model.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class WeatherService {

    @Autowired
    @Lazy
    private WeatherMonitoringService weatherMonitoringService;
    
    private final Random random = new Random();
    
    // Sample weather data for demonstration
    private final Map<String, Weather> weatherCache = new HashMap<>();
    
    public WeatherService() {
        // Initialize with some sample data
        initializeSampleData();
    }
    
    public Weather getWeatherByCity(String city) {
        String cityKey = city.toLowerCase().trim();
        
        // Return cached data if available
        if (weatherCache.containsKey(cityKey)) {
            Weather cached = weatherCache.get(cityKey);
            // Add some randomness to make it more realistic
            cached.setTemperature(cached.getTemperature() + (random.nextDouble() - 0.5) * 5);
            cached.setTimestamp(System.currentTimeMillis());
            return cached;
        }
        
        // Generate random weather data for unknown cities
        return generateRandomWeather(city);
    }
    
    private void initializeSampleData() {
        weatherCache.put("london", new Weather("London", "UK", 288.15, "Partly cloudy", 65, 12.5, "02d"));
        weatherCache.put("new york", new Weather("New York", "US", 295.15, "Clear sky", 45, 8.2, "01d"));
        weatherCache.put("tokyo", new Weather("Tokyo", "JP", 298.15, "Light rain", 78, 5.1, "10d"));
        weatherCache.put("paris", new Weather("Paris", "FR", 285.15, "Overcast", 72, 15.3, "04d"));
        weatherCache.put("sydney", new Weather("Sydney", "AU", 302.15, "Sunny", 55, 18.7, "01d"));
        weatherCache.put("moscow", new Weather("Moscow", "RU", 275.15, "Snow", 85, 22.1, "13d"));
        weatherCache.put("mumbai", new Weather("Mumbai", "IN", 305.15, "Hot and humid", 88, 6.4, "01d"));
        weatherCache.put("berlin", new Weather("Berlin", "DE", 283.15, "Cloudy", 68, 11.2, "03d"));
    }
    
    private Weather generateRandomWeather(String city) {
        String[] descriptions = {"Clear sky", "Partly cloudy", "Cloudy", "Light rain", "Heavy rain", "Snow", "Sunny", "Overcast"};
        String[] icons = {"01d", "02d", "03d", "04d", "09d", "10d", "11d", "13d"};
        String[] countries = {"US", "UK", "CA", "AU", "DE", "FR", "IT", "ES", "JP", "CN"};
        
        double temperature = 273.15 + (random.nextDouble() * 40 - 10); // -10°C to 30°C
        String description = descriptions[random.nextInt(descriptions.length)];
        double humidity = 30 + random.nextDouble() * 70; // 30% to 100%
        double windSpeed = random.nextDouble() * 25; // 0 to 25 m/s
        String icon = icons[random.nextInt(icons.length)];
        String country = countries[random.nextInt(countries.length)];
        
        Weather weather = new Weather(city, country, temperature, description, humidity, windSpeed, icon);
        
        // Cache the generated weather
        weatherCache.put(city.toLowerCase().trim(), weather);
        
        return weather;
    }
    
    public Weather getCurrentWeather() {
        // Return weather for a default city
        return getWeatherByCity("London");
    }
}