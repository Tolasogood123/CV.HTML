package com.farmers.general.harvest.dto;

import com.farmers.general.harvest.enums.HarvestQuality;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HarvestResponse {

    private Long id;
    private Long farmId;
    private Long fieldId;
    private Long cropId;
    private Long recordedBy;
    private LocalDate harvestDate;
    private Double quantity;
    private String unit;
    private HarvestQuality quality;
    private BigDecimal estimatedValue;
    private BigDecimal actualRevenue;
    private Long buyerId;
    private String notes;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
