package com.farmers.general.report.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {

    private FarmerDashboard farmer;
    private AdminDashboard admin;

    @Data
    public static class FarmerDashboard {
        private WeatherSection weather;
        private InventorySection inventory;
        private NotificationSection notifications;
        private List<UpcomingActivity> upcomingActivities;
    }

    @Data
    public static class AdminDashboard {
        private RevenueSection revenue;
        private ExpenseSection expenses;
        private ProductionSection production;
        private MarketplaceSection marketplace;
        private GeographicSection geographic;
        private List<CriticalAlert> criticalAlerts;
    }

    @Data
    public static class WeatherSection {
        private Double temperature;
        private String condition;
        private Double humidity;
        private String location;
    }

    @Data
    public static class InventorySection {
        private long totalItems;
        private long lowStockItems;
        private List<String> lowStockItemNames;
    }

    @Data
    public static class NotificationSection {
        private long unreadCount;
    }

    @Data
    public static class UpcomingActivity {
        private String type;
        private String description;
        private String dueDate;
    }

    @Data
    public static class RevenueSection {
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private BigDecimal averageOrderValue;
        private long totalOrders;
    }

    @Data
    public static class ExpenseSection {
        private BigDecimal totalExpenses;
        private BigDecimal monthlyExpenses;
        private List<ExpenseByCategory> byCategory;
    }

    @Data
    public static class ExpenseByCategory {
        private String category;
        private BigDecimal amount;
        private Double percentage;
    }

    @Data
    public static class ProductionSection {
        private long totalHarvests;
        private Double totalQuantity;
        private Double averageQuality;
    }

    @Data
    public static class MarketplaceSection {
        private long activeProducts;
        private long pendingProducts;
        private long totalSellers;
    }

    @Data
    public static class GeographicSection {
        private long totalFarms;
        private long totalFields;
    }

    @Data
    public static class CriticalAlert {
        private String type;
        private String message;
        private String severity;
    }
}
