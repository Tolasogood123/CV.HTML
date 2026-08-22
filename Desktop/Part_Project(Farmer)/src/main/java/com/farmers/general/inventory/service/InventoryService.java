package com.farmers.general.inventory.service;

import com.farmers.general.exception.BadRequestException;
import com.farmers.general.exception.ResourceNotFoundException;
import com.farmers.general.inventory.dto.InventoryItemRequest;
import com.farmers.general.inventory.dto.InventoryItemResponse;
import com.farmers.general.inventory.dto.InventoryTransactionRequest;
import com.farmers.general.inventory.dto.InventoryTransactionResponse;
import com.farmers.general.inventory.entity.InventoryItem;
import com.farmers.general.inventory.entity.InventoryTransaction;
import com.farmers.general.inventory.enums.TransactionType;
import com.farmers.general.inventory.repository.InventoryItemRepository;
import com.farmers.general.inventory.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository itemRepository;
    private final InventoryTransactionRepository transactionRepository;

    // TODO: inject NotificationService and call generateLowStockNotification() when stock drops below minimum

    public List<InventoryItemResponse> getItemsByFarm(Long farmId) {
        return itemRepository.findByFarmIdAndDeletedFalse(farmId).stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    public InventoryItemResponse getItemById(Long id) {
        InventoryItem item = itemRepository.findById(id)
                .filter(i -> !i.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item", id));
        return toItemResponse(item);
    }

    public InventoryItemResponse createItem(Long farmId, InventoryItemRequest request) {
        InventoryItem item = InventoryItem.builder()
                .farmId(farmId)
                .name(request.getName())
                .category(request.getCategory())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0.0)
                .unit(request.getUnit())
                .minimumStock(request.getMinimumStock())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();

        item = itemRepository.save(item);
        return toItemResponse(item);
    }

    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = itemRepository.findById(id)
                .filter(i -> !i.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item", id));

        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setUnit(request.getUnit());
        item.setMinimumStock(request.getMinimumStock());
        item.setLocation(request.getLocation());
        item.setDescription(request.getDescription());

        item = itemRepository.save(item);
        InventoryItemResponse response = toItemResponse(item);

        checkLowStock(item);
        return response;
    }

    @Transactional
    public void deleteItem(Long id) {
        InventoryItem item = itemRepository.findById(id)
                .filter(i -> !i.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item", id));
        item.setDeleted(true);
        itemRepository.save(item);
    }

    @Transactional
    public InventoryTransactionResponse recordTransaction(Long itemId, Long userId, InventoryTransactionRequest request) {
        InventoryItem item = itemRepository.findById(itemId)
                .filter(i -> !i.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item", itemId));

        switch (request.getType()) {
            case STOCK_IN:
                item.setQuantity(item.getQuantity() + request.getQuantity());
                break;
            case STOCK_OUT:
                if (item.getQuantity() < request.getQuantity()) {
                    throw new BadRequestException("Insufficient stock. Available: " + item.getQuantity());
                }
                item.setQuantity(item.getQuantity() - request.getQuantity());
                break;
            case ADJUSTMENT:
                item.setQuantity(request.getQuantity());
                break;
        }

        InventoryTransaction transaction = InventoryTransaction.builder()
                .inventoryItem(item)
                .type(request.getType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .createdBy(userId)
                .build();

        transaction = transactionRepository.save(transaction);
        itemRepository.save(item);

        checkLowStock(item);

        InventoryTransactionResponse response = new InventoryTransactionResponse();
        response.setId(transaction.getId());
        response.setInventoryItemId(item.getId());
        response.setItemName(item.getName());
        response.setType(transaction.getType());
        response.setQuantity(transaction.getQuantity());
        response.setReason(transaction.getReason());
        response.setCreatedBy(transaction.getCreatedBy());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    public List<InventoryTransactionResponse> getItemTransactions(Long itemId) {
        return transactionRepository.findByInventoryItemIdOrderByCreatedAtDesc(itemId).stream()
                .map(t -> {
                    InventoryTransactionResponse response = new InventoryTransactionResponse();
                    response.setId(t.getId());
                    response.setInventoryItemId(t.getInventoryItem().getId());
                    response.setItemName(t.getInventoryItem().getName());
                    response.setType(t.getType());
                    response.setQuantity(t.getQuantity());
                    response.setReason(t.getReason());
                    response.setCreatedBy(t.getCreatedBy());
                    response.setCreatedAt(t.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private void checkLowStock(InventoryItem item) {
        if (item.getMinimumStock() != null && item.getQuantity() <= item.getMinimumStock()) {
            // TODO: trigger LOW_INVENTORY notification via NotificationService
        }
    }

    private InventoryItemResponse toItemResponse(InventoryItem item) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(item.getId());
        response.setFarmId(item.getFarmId());
        response.setName(item.getName());
        response.setCategory(item.getCategory());
        response.setQuantity(item.getQuantity());
        response.setUnit(item.getUnit());
        response.setMinimumStock(item.getMinimumStock());
        response.setLocation(item.getLocation());
        response.setDescription(item.getDescription());
        response.setLowStock(item.getMinimumStock() != null && item.getQuantity() <= item.getMinimumStock());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }
}
