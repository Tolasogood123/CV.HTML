package com.farmers.general.inventory.controller;

import com.farmers.general.inventory.dto.InventoryItemRequest;
import com.farmers.general.inventory.dto.InventoryItemResponse;
import com.farmers.general.inventory.dto.InventoryTransactionRequest;
import com.farmers.general.inventory.dto.InventoryTransactionResponse;
import com.farmers.general.inventory.service.InventoryService;
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
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/farms/{farmId}/inventory")
    @Operation(summary = "Get all inventory items for a farm")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getItems(@PathVariable Long farmId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemsByFarm(farmId)));
    }

    @GetMapping("/inventory/{id}")
    @Operation(summary = "Get inventory item by ID")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemById(id)));
    }

    @PostMapping("/farms/{farmId}/inventory")
    @Operation(summary = "Create an inventory item")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> createItem(
            @PathVariable Long farmId,
            @Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse response = inventoryService.createItem(farmId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Item created", response));
    }

    @PutMapping("/inventory/{id}")
    @Operation(summary = "Update an inventory item")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated", inventoryService.updateItem(id, request)));
    }

    @DeleteMapping("/inventory/{id}")
    @Operation(summary = "Soft delete an inventory item")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted", null));
    }

    @PostMapping("/inventory/{itemId}/transactions")
    @Operation(summary = "Record a stock transaction (in/out/adjustment)")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> recordTransaction(
            @PathVariable Long itemId,
            @Valid @RequestBody InventoryTransactionRequest request) {
        // TODO: replace hardcoded userId with authenticated user
        InventoryTransactionResponse response = inventoryService.recordTransaction(itemId, 1L, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Transaction recorded", response));
    }

    @GetMapping("/inventory/{itemId}/transactions")
    @Operation(summary = "Get transaction history for an item")
    public ResponseEntity<ApiResponse<List<InventoryTransactionResponse>>> getItemTransactions(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemTransactions(itemId)));
    }
}
