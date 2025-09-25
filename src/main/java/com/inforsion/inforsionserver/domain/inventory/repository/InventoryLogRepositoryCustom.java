package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryLogEntity;
import com.inforsion.inforsionserver.global.enums.InventoryLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryLogRepositoryCustom {
    
    Page<InventoryLogEntity> findLogsByInventoryIdWithPaging(Integer inventoryId, Pageable pageable);
    
    Page<InventoryLogEntity> findLogsByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    List<InventoryLogEntity> findLogsByInventoryAndTypeAndDateRange(Integer inventoryId, InventoryLogType logType,
                                                                   LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryLogEntity> findLogsByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryLogEntity> findRecentLogsByInventoryId(Integer inventoryId, int limit);
}