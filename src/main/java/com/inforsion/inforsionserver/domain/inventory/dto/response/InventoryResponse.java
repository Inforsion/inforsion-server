package com.inforsion.inforsionserver.domain.inventory.dto.response;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.global.enums.StockStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "재고 응답")
@Getter
@Builder
public class InventoryResponse {

    @Schema(description = "재고 ID", example = "1")
    private final Integer id;

    @Schema(description = "매장 ID", example = "1")
    private final Integer storeId;

    @Schema(description = "매장명", example = "강남점")
    private final String storeName;

    @Schema(description = "재료 ID", example = "1")
    private final Integer ingredientId;

    @Schema(description = "재료명", example = "우유")
    private final String ingredientName;

    @Schema(description = "단위", example = "ml")
    private final String unit;

    @Schema(description = "현재 재고량", example = "1000.00")
    private final BigDecimal currentStock;

    @Schema(description = "최소 재고 수준", example = "100.00")
    private final BigDecimal minStock;

    @Schema(description = "최대 재고 수준", example = "5000.00")
    private final BigDecimal maxStock;

    @Schema(description = "단위 당 가격", example = "3000")
    private final BigDecimal unitCost;

    @Schema(description = "유통기한", example = "2025-12-31")
    private final LocalDate expiryDate;

    @Schema(description = "마지막 입고일", example = "2025-12-14")
    private final LocalDate lastRestockedDate;

    @Schema(description = "재고 상태", example = "SUFFICIENT")
    private final StockStatus stockStatus;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public static InventoryResponse from(InventoryEntity entity) {
        return InventoryResponse.builder()
                .id(entity.getId())
                .storeId(entity.getStore() != null ? entity.getStore().getId() : null)
                .storeName(entity.getStore() != null ? entity.getStore().getName() : null)
                .ingredientId(entity.getIngredient() != null ? entity.getIngredient().getId() : null)
                .ingredientName(entity.getName())
                .unit(entity.getUnit())
                .currentStock(entity.getCurrentStock())
                .minStock(entity.getMinStock())
                .maxStock(entity.getMaxStock())
                .unitCost(entity.getUnitCost())
                .expiryDate(entity.getExpiryDate())
                .lastRestockedDate(entity.getLastRestockedDate())
                .stockStatus(entity.getStockStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}