package com.farmers.general.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ReportResponse {

    private Map<String, Object> summary;
    private List<Map<String, Object>> details;

    @Data
    public static class FarmReport {
        private long totalFarms;
        private long activeFarms;
        private Map<String, Long> farmsByProvince;
    }

    @Data
    public static class CropReport {
        private long totalCrops;
        private Map<String, Long> cropsByType;
    }

    @Data
    public static class ProductionReport {
        private long totalHarvests;
        private Double totalQuantity;
        private Map<String, Double> productionByCrop;
    }

    @Data
    public static class WorkerReport {
        private long totalWorkers;
        private Map<String, Long> workersByRole;
    }

    @Data
    public static class FinanceReport {
        private BigDecimal totalRevenue;
        private BigDecimal totalExpenses;
        private BigDecimal netProfit;
        private List<Map<String, Object>> monthlyTrend;
    }

    @Data
    public static class MarketplaceReport {
        private long totalProducts;
        private long activeProducts;
        private long totalOrders;
        private BigDecimal totalOrderValue;
    }
}
