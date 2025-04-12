package com.Hinga.farmMis.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;
    private final RestTemplate restTemplate=new RestTemplate();

    public String getWeather(String location) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + location +
                "&appid=" + apiKey + "&units=metric";
        return restTemplate.getForObject(url, String.class);
    }
}
