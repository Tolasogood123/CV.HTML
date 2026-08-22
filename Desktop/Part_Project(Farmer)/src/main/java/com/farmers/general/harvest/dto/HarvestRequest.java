package com.farmers.general.harvest.dto;

import com.farmers.general.harvest.enums.HarvestQuality;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HarvestRequest {

    private Long fieldId;
    private Long cropId;

    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must not be negative")
    private Double quantity;

    @NotNull(message = "Unit is required")
    private String unit;

    private HarvestQuality quality;

    @PositiveOrZero(message = "Estimated value must not be negative")
    private BigDecimal estimatedValue;

    @PositiveOrZero(message = "Actual revenue must not be negative")
    private BigDecimal actualRevenue;

    private Long buyerId;
    private String notes;
    private String imageUrl;
}
