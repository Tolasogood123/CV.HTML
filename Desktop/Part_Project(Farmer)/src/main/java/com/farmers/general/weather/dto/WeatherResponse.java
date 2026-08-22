package com.farmers.general.weather.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {

    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double rainProbability;
    private String condition;
    private List<ForecastDay> forecast;

    @Data
    public static class ForecastDay {
        private String date;
        private Double maxTemp;
        private Double minTemp;
        private Double rainProbability;
        private String condition;
    }
}
