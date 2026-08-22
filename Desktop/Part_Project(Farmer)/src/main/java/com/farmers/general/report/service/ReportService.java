package com.farmers.general.report.service;

import com.farmers.general.report.dto.DashboardResponse;
import com.farmers.general.report.dto.ReportResponse;
import com.farmers.general.inventory.repository.InventoryItemRepository;
import com.farmers.general.expense.repository.ExpenseRepository;
import com.farmers.general.harvest.repository.HarvestRepository;
import com.farmers.general.marketplace.repository.ProductRepository;
import com.farmers.general.marketplace.repository.OrderRepository;
import com.farmers.general.notification.service.NotificationService;
import com.farmers.general.weather.service.WeatherService;
import com.farmers.general.weather.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ExpenseRepository expenseRepository;
    private final HarvestRepository harvestRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final WeatherService weatherService;

    // --- Dashboard ---

    public DashboardResponse.FarmerDashboard getFarmerDashboard(Long userId, double lat, double lng) {
        DashboardResponse.FarmerDashboard dashboard = new DashboardResponse.FarmerDashboard();

        // Weather
        WeatherResponse weather = weatherService.getWeather(lat, lng);
        DashboardResponse.WeatherSection weatherSection = new DashboardResponse.WeatherSection();
        weatherSection.setTemperature(weather.getTemperature());
        weatherSection.setCondition(weather.getCondition());
        weatherSection.setHumidity(weather.getHumidity());
        dashboard.setWeather(weatherSection);

        // Notifications
        DashboardResponse.NotificationSection notifSection = new DashboardResponse.NotificationSection();
        notifSection.setUnreadCount(notificationService.getUnreadCount(userId));
        dashboard.setNotifications(notifSection);

        return dashboard;
    }

    public DashboardResponse.AdminDashboard getAdminDashboard() {
        DashboardResponse.AdminDashboard dashboard = new DashboardResponse.AdminDashboard();

        // Marketplace stats
        DashboardResponse.MarketplaceSection marketplaceSection = new DashboardResponse.MarketplaceSection();
        marketplaceSection.setActiveProducts(productRepository.findByDeletedFalseAndStatus(
                com.farmers.general.marketplace.enums.ProductStatus.ACTIVE).size());
        marketplaceSection.setPendingProducts(productRepository.findByDeletedFalseAndStatus(
                com.farmers.general.marketplace.enums.ProductStatus.PENDING).size());
        dashboard.setMarketplace(marketplaceSection);

        // Production stats
        DashboardResponse.ProductionSection productionSection = new DashboardResponse.ProductionSection();
        productionSection.setTotalHarvests(harvestRepository.findByFarmIdAndDeletedFalseOrderByHarvestDateDesc(1L).size());
        dashboard.setProduction(productionSection);

        // Finance stats
        DashboardResponse.RevenueSection revenueSection = new DashboardResponse.RevenueSection();
        dashboard.setRevenue(revenueSection);

        DashboardResponse.ExpenseSection expenseSection = new DashboardResponse.ExpenseSection();
        expenseSection.setTotalExpenses(BigDecimal.ZERO);
        dashboard.setExpenses(expenseSection);

        return dashboard;
    }

    // --- Reports ---

    public ReportResponse.FarmReport getFarmReport(Long farmId, LocalDate from, LocalDate to) {
        ReportResponse.FarmReport report = new ReportResponse.FarmReport();
        report.setTotalFarms(1L);
        report.setActiveFarms(1L);
        report.setFarmsByProvince(new HashMap<>());
        return report;
    }

    public ReportResponse.CropReport getCropReport(Long farmId) {
        ReportResponse.CropReport report = new ReportResponse.CropReport();
        report.setTotalCrops(0L);
        report.setCropsByType(new HashMap<>());
        return report;
    }

    public ReportResponse.ProductionReport getProductionReport(Long farmId, Long cropId) {
        ReportResponse.ProductionReport report = new ReportResponse.ProductionReport();
        report.setTotalHarvests(harvestRepository.findByFarmIdAndDeletedFalseOrderByHarvestDateDesc(farmId).size());
        report.setTotalQuantity(0.0);
        report.setProductionByCrop(new HashMap<>());
        return report;
    }

    public ReportResponse.FinanceReport getFinanceReport(Long farmId, LocalDate from, LocalDate to) {
        ReportResponse.FinanceReport report = new ReportResponse.FinanceReport();
        report.setTotalExpenses(BigDecimal.ZERO);
        report.setTotalRevenue(BigDecimal.ZERO);
        report.setNetProfit(BigDecimal.ZERO);
        report.setMonthlyTrend(new ArrayList<>());
        return report;
    }

    public ReportResponse.MarketplaceReport getMarketplaceReport() {
        ReportResponse.MarketplaceReport report = new ReportResponse.MarketplaceReport();
        report.setTotalProducts(productRepository.findByDeletedFalse().size());
        report.setActiveProducts(productRepository.findByDeletedFalseAndStatus(
                com.farmers.general.marketplace.enums.ProductStatus.ACTIVE).size());
        return report;
    }

    public ReportResponse.WorkerReport getWorkerReport(Long farmId) {
        ReportResponse.WorkerReport report = new ReportResponse.WorkerReport();
        report.setTotalWorkers(0L);
        report.setWorkersByRole(new HashMap<>());
        return report;
    }
}
