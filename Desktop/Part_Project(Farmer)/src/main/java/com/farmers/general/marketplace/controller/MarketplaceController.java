package com.farmers.general.marketplace.controller;

import com.farmers.general.marketplace.dto.*;
import com.farmers.general.marketplace.enums.OrderStatus;
import com.farmers.general.marketplace.service.MarketplaceService;
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
@Tag(name = "Marketplace", description = "Marketplace products and orders endpoints")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    // --- Products ---

    @GetMapping("/products")
    @Operation(summary = "Get all active products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getActiveProducts() {
        return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getAllActiveProducts()));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getProductById(id)));
    }

    @GetMapping("/sellers/{sellerId}/products")
    @Operation(summary = "Get products by seller")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSellerProducts(@PathVariable Long sellerId) {
        return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getSellerProducts(sellerId)));
    }

    @PostMapping("/sellers/{sellerId}/products")
    @Operation(summary = "Create a product (starts as PENDING)")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @PathVariable Long sellerId,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = marketplaceService.createProduct(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Product created", response));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update a product (seller only)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestParam Long sellerId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Product updated", marketplaceService.updateProduct(id, sellerId, request)));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Soft delete a product (seller only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @RequestParam Long sellerId) {
        marketplaceService.deleteProduct(id, sellerId);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted", null));
    }

    @PatchMapping("/products/{id}/approve")
    @Operation(summary = "Approve a product (admin only)")
    public ResponseEntity<ApiResponse<ProductResponse>> approveProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Product approved", marketplaceService.approveProduct(id)));
    }

    @PatchMapping("/products/{id}/reject")
    @Operation(summary = "Reject a product (admin only)")
    public ResponseEntity<ApiResponse<ProductResponse>> rejectProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Product rejected", marketplaceService.rejectProduct(id)));
    }

    // --- Orders ---

    @PostMapping("/orders")
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestParam Long buyerId,
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = marketplaceService.createOrder(buyerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Order created", response));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get orders by buyer or seller")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId) {
        if (buyerId != null) {
            return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getBuyerOrders(buyerId)));
        } else if (sellerId != null) {
            return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getSellerOrders(sellerId)));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Provide buyerId or sellerId"));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marketplaceService.getOrderById(id)));
    }

    @PatchMapping("/orders/{id}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Order status updated", marketplaceService.updateOrderStatus(id, status)));
    }
}
