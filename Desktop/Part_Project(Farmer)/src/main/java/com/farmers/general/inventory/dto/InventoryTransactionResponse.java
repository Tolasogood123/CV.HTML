package com.farmers.general.inventory.dto;

import com.farmers.general.inventory.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryTransactionResponse {

    private Long id;
    private Long inventoryItemId;
    private String itemName;
    private TransactionType type;
    private Double quantity;
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
