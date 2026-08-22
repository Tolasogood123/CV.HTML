package com.farmers.general.weather.service;

import com.farmers.general.weather.dto.FarmingRecommendation;
import com.farmers.general.weather.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key:}")
    private String apiKey;

    @Value("${weather.api.base-url:}")
    private String baseUrl;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeather(double lat, double lng) {
        if (apiKey == null || apiKey.isBlank()) {
            return getMockWeather();
        }

        String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
                baseUrl, lat, lng, apiKey);

        try {
            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (Exception e) {
            return getMockWeather();
        }
    }

    public FarmingRecommendation.RecommendationList getFarmingRecommendations(WeatherResponse weather) {
        List<FarmingRecommendation> recommendations = new ArrayList<>();

        if (weather.getRainProbability() != null && weather.getRainProbability() > 70) {
            FarmingRecommendation rec = new FarmingRecommendation();
            rec.setCategory("IRRIGATION");
            rec.setRecommendation("Reduce or skip irrigation today due to high rain probability.");
            rec.setSeverity("HIGH");
            recommendations.add(rec);
        }

        if (weather.getTemperature() != null && weather.getTemperature() > 35) {
            FarmingRecommendation rec = new FarmingRecommendation();
            rec.setCategory("HEAT_STRESS");
            rec.setRecommendation("Provide shade for livestock and consider early morning field work.");
            rec.setSeverity("HIGH");
            recommendations.add(rec);
        }

        if (weather.getTemperature() != null && weather.getTemperature() < 5) {
            FarmingRecommendation rec = new FarmingRecommendation();
            rec.setCategory("FROST");
            rec.setRecommendation("Frost warning: protect sensitive crops and cover seedlings.");
            rec.setSeverity("HIGH");
            recommendations.add(rec);
        }

        if (weather.getWindSpeed() != null && weather.getWindSpeed() > 40) {
            FarmingRecommendation rec = new FarmingRecommendation();
            rec.setCategory("WIND");
            rec.setRecommendation("Avoid spraying pesticides or fertilizers in high winds.");
            rec.setSeverity("MEDIUM");
            recommendations.add(rec);
        }

        if (weather.getHumidity() != null && weather.getHumidity() > 85) {
            FarmingRecommendation rec = new FarmingRecommendation();
            rec.setCategory("HUMIDITY");
            rec.setRecommendation("High humidity increases disease risk. Monitor crops for fungal issues.");
            rec.setSeverity("MEDIUM");
            recommendations.add(rec);
        }

        FarmingRecommendation.RecommendationList list = new FarmingRecommendation.RecommendationList();
        list.setRecommendations(recommendations);
        list.setGeneralAdvice(recommendations.isEmpty()
                ? "Weather conditions are favorable for farming activities."
                : "Review the recommendations above and adjust your farming plan accordingly.");
        return list;
    }

    private WeatherResponse getMockWeather() {
        WeatherResponse response = new WeatherResponse();
        response.setTemperature(25.0);
        response.setHumidity(60.0);
        response.setWindSpeed(10.0);
        response.setRainProbability(20.0);
        response.setCondition("Partly Cloudy");
        response.setForecast(new ArrayList<>());
        return response;
    }
}
