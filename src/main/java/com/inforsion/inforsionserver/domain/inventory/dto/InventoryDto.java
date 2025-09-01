package com.inforsion.inforsionserver.domain.inventory.dto;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Integer id;

    private String name; // 재료명

    private BigDecimal currentStock; // 현재 재료량

    private BigDecimal minStock; // 최대 재고 수준

    private BigDecimal maxStock; // 최소 재고 수준

    private String unit; // 단위 (g, ml, 개 등)

    private BigDecimal unitCost; // 단위 당 가격

    private LocalDateTime expiryDate; // 유통기한

    private LocalDateTime lastRestockedDate; // 마지막 입고일

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime updatedAt; // 수정일

    private Integer storeId;

    public static InventoryDto fromEntity(InventoryEntity entity) {
        return new InventoryDto(
                entity.getId(),
                entity.getName(),
                entity.getCurrentStock(),
                entity.getMinStock(),
                entity.getMaxStock(),
                entity.getUnit(),
                entity.getUnitCost(),
                entity.getExpiryDate(),
                entity.getLastRestockedDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),

                entity.getStore() != null ? entity.getStore().getId() : null
        );
    }
}
