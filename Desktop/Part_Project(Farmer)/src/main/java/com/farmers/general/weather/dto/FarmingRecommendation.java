package com.farmers.general.weather.dto;

import lombok.Data;

import java.util.List;

@Data
public class FarmingRecommendation {

    private String category;
    private String recommendation;
    private String severity;

    @Data
    public static class RecommendationList {
        private List<FarmingRecommendation> recommendations;
        private String generalAdvice;
    }
}
