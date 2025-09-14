package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.dto.InventoryDto;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface InventoryRepositoryCustom {
    Page<InventoryEntity> findInventories(Integer storeId, Pageable pageable);
    Long updateInventory(Integer inventoryId, InventoryDto inventoryDto);
    Long deleteInventory(Integer inventoryId);
    List<InventoryEntity> findItemsExpiringBefore(LocalDate targetDate);
}