package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.services.WeatherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:5173")

public class WeatherController {
    private  WeatherService weatherService;

    public WeatherController(WeatherService weatherService){
        this.weatherService=weatherService;
    }

    @GetMapping
    public String getWeather(@RequestParam String location) {
        return weatherService.getWeather(location);
    }

}
