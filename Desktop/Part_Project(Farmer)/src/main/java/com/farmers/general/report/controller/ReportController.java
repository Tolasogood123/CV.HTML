package com.farmers.general.report.controller;

import com.farmers.general.report.dto.DashboardResponse;
import com.farmers.general.report.dto.ReportResponse;
import com.farmers.general.report.service.ReportService;
import com.farmers.general.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reports & Dashboards", description = "Reporting and dashboard endpoints")
public class ReportController {

    private final ReportService reportService;

    // --- Dashboards ---

    @GetMapping("/dashboard/farmer")
    @Operation(summary = "Farmer dashboard (weather, inventory, notifications)")
    public ResponseEntity<ApiResponse<DashboardResponse.FarmerDashboard>> getFarmerDashboard(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") double lat,
            @RequestParam(defaultValue = "0") double lng) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getFarmerDashboard(userId, lat, lng)));
    }

    @GetMapping("/dashboard/admin")
    @Operation(summary = "Admin dashboard (revenue, expenses, production, marketplace)")
    public ResponseEntity<ApiResponse<DashboardResponse.AdminDashboard>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getAdminDashboard()));
    }

    // --- Reports ---

    @GetMapping("/reports/farms")
    @Operation(summary = "Farm report")
    public ResponseEntity<ApiResponse<ReportResponse.FarmReport>> getFarmReport(
            @RequestParam Long farmId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getFarmReport(farmId, from, to)));
    }

    @GetMapping("/reports/crops")
    @Operation(summary = "Crops report")
    public ResponseEntity<ApiResponse<ReportResponse.CropReport>> getCropReport(@RequestParam Long farmId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getCropReport(farmId)));
    }

    @GetMapping("/reports/production")
    @Operation(summary = "Production report")
    public ResponseEntity<ApiResponse<ReportResponse.ProductionReport>> getProductionReport(
            @RequestParam Long farmId,
            @RequestParam(required = false) Long cropId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getProductionReport(farmId, cropId)));
    }

    @GetMapping("/reports/workers")
    @Operation(summary = "Workers report")
    public ResponseEntity<ApiResponse<ReportResponse.WorkerReport>> getWorkerReport(@RequestParam Long farmId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getWorkerReport(farmId)));
    }

    @GetMapping("/reports/finance")
    @Operation(summary = "Finance report")
    public ResponseEntity<ApiResponse<ReportResponse.FinanceReport>> getFinanceReport(
            @RequestParam Long farmId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getFinanceReport(farmId, from, to)));
    }

    @GetMapping("/reports/marketplace")
    @Operation(summary = "Marketplace report")
    public ResponseEntity<ApiResponse<ReportResponse.MarketplaceReport>> getMarketplaceReport() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMarketplaceReport()));
    }
}
