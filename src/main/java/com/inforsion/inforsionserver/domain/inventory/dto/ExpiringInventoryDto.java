package com.inforsion.inforsionserver.domain.inventory.dto;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 유통기한 임박 재고 조회용
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringInventoryDto {
    private Integer id;
    private String name;
    private BigDecimal currentStock;
    private LocalDate expiryDate;

    public static ExpiringInventoryDto fromEntity(InventoryEntity entity) {
        return new ExpiringInventoryDto(
                entity.getId(),
                entity.getName(),
                entity.getCurrentStock(),
                entity.getExpiryDate()
        );
    }
}
