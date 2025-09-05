package com.apad.weather.controller;

import com.apad.weather.model.Weather;
import com.apad.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

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
        return weatherService.getWeatherByCity(city);
    }
}