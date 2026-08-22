package com.farmers.general.inventory.dto;

import com.farmers.general.inventory.enums.InventoryCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryItemRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category is required")
    private InventoryCategory category;

    @PositiveOrZero(message = "Quantity must not be negative")
    private Double quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @PositiveOrZero(message = "Minimum stock must not be negative")
    private Double minimumStock;

    private String location;
    private String description;
}
