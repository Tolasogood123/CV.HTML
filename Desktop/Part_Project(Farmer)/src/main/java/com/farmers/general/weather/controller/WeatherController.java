package com.farmers.general.weather.controller;

import com.farmers.general.response.ApiResponse;
import com.farmers.general.weather.dto.FarmingRecommendation;
import com.farmers.general.weather.dto.WeatherResponse;
import com.farmers.general.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather and farming recommendations endpoints")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    @Operation(summary = "Get weather data for a location")
    public ResponseEntity<ApiResponse<WeatherResponse>> getWeather(
            @RequestParam double lat,
            @RequestParam double lng) {
        return ResponseEntity.ok(ApiResponse.ok(weatherService.getWeather(lat, lng)));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get farming recommendations based on weather")
    public ResponseEntity<ApiResponse<FarmingRecommendation.RecommendationList>> getRecommendations(
            @RequestParam double lat,
            @RequestParam double lng) {
        WeatherResponse weather = weatherService.getWeather(lat, lng);
        return ResponseEntity.ok(ApiResponse.ok(weatherService.getFarmingRecommendations(weather)));
    }
}
