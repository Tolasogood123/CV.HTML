package com.farmers.general.inventory.dto;

import com.farmers.general.inventory.enums.InventoryCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryItemResponse {

    private Long id;
    private Long farmId;
    private String name;
    private InventoryCategory category;
    private Double quantity;
    private String unit;
    private Double minimumStock;
    private String location;
    private String description;
    private Boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
