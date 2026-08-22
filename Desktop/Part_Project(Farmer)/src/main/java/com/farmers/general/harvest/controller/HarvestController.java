package com.farmers.general.harvest.controller;

import com.farmers.general.harvest.dto.HarvestRequest;
import com.farmers.general.harvest.dto.HarvestResponse;
import com.farmers.general.harvest.service.HarvestService;
import com.farmers.general.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Harvests", description = "Harvest recording endpoints")
public class HarvestController {

    private final HarvestService harvestService;

    @GetMapping("/farms/{farmId}/harvests")
    @Operation(summary = "Get all harvests for a farm")
    public ResponseEntity<ApiResponse<List<HarvestResponse>>> getHarvests(@PathVariable Long farmId) {
        return ResponseEntity.ok(ApiResponse.ok(harvestService.getHarvests(farmId)));
    }

    @GetMapping("/harvests/{id}")
    @Operation(summary = "Get harvest by ID")
    public ResponseEntity<ApiResponse<HarvestResponse>> getHarvest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(harvestService.getHarvestById(id)));
    }

    @PostMapping("/farms/{farmId}/harvests")
    @Operation(summary = "Record a harvest")
    public ResponseEntity<ApiResponse<HarvestResponse>> createHarvest(
            @PathVariable Long farmId,
            @Valid @RequestBody HarvestRequest request) {
        // TODO: replace hardcoded userId with authenticated user
        HarvestResponse response = harvestService.createHarvest(farmId, 1L, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Harvest recorded", response));
    }

    @PutMapping("/harvests/{id}")
    @Operation(summary = "Update a harvest")
    public ResponseEntity<ApiResponse<HarvestResponse>> updateHarvest(
            @PathVariable Long id,
            @Valid @RequestBody HarvestRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Harvest updated", harvestService.updateHarvest(id, request)));
    }

    @DeleteMapping("/harvests/{id}")
    @Operation(summary = "Soft delete a harvest")
    public ResponseEntity<ApiResponse<Void>> deleteHarvest(@PathVariable Long id) {
        harvestService.deleteHarvest(id);
        return ResponseEntity.ok(ApiResponse.ok("Harvest deleted", null));
    }
}
