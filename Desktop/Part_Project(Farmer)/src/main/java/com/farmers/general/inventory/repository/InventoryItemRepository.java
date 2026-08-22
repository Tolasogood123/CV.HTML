package com.farmers.general.inventory.repository;

import com.farmers.general.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByFarmIdAndDeletedFalse(Long farmId);

    @Query("SELECT i FROM InventoryItem i WHERE i.farmId = :farmId AND i.deleted = false AND i.quantity <= i.minimumStock")
    List<InventoryItem> findLowStockItems(@Param("farmId") Long farmId);

    List<InventoryItem> findByFarmIdAndCategoryAndDeletedFalse(Long farmId, com.farmers.general.inventory.enums.InventoryCategory category);
}
