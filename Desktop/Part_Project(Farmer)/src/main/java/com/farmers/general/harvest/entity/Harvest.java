package com.farmers.general.harvest.entity;

import com.farmers.general.harvest.enums.HarvestQuality;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "harvests", indexes = {
    @Index(name = "idx_harvests_farm_id", columnList = "farm_id"),
    @Index(name = "idx_harvests_harvest_date", columnList = "harvest_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long farmId;

    private Long fieldId;

    private Long cropId;

    @Column(nullable = false)
    private Long recordedBy;

    @Column(nullable = false)
    private LocalDate harvestDate;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String unit;

    @Enumerated(EnumType.STRING)
    private HarvestQuality quality;

    @Column(precision = 12, scale = 2)
    private BigDecimal estimatedValue;

    @Column(precision = 12, scale = 2)
    private BigDecimal actualRevenue;

    private Long buyerId;

    private String notes;

    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
