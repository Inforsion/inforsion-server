package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryRepositoryCustom {
    
    List<InventoryEntity> findLowStockItems(Integer storeId, BigDecimal threshold);
    
    List<InventoryEntity> findExpiringItems(Integer storeId, LocalDateTime beforeDate);
    
    BigDecimal getTotalQuantityByIngredientAndStore(Integer ingredientId, Integer storeId);
    
    List<InventoryEntity> findInventoryByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);
    
    Long countExpiredInventoryByStoreId(Integer storeId);
}