package com.inforsion.inforsionserver.domain.ingredient.dto.response;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "재료 응답")
@Getter
@Builder
public class IngredientResponse {

    @Schema(description = "재료 ID", example = "1")
    private final Integer id;

    @Schema(description = "매장 ID", example = "1")
    private final Integer storeId;

    @Schema(description = "매장명", example = "강남점")
    private final String storeName;

    @Schema(description = "재료명", example = "우유")
    private final String name;

    @Schema(description = "재고 가격", example = "3000.00")
    private final BigDecimal stockPrice;

    @Schema(description = "1개당 재고 용량", example = "1000.00")
    private final BigDecimal unitCapacity;

    @Schema(description = "재고 수", example = "10")
    private final Integer stockQuantity;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;

    public static IngredientResponse from(IngredientEntity entity) {
        return IngredientResponse.builder()
                .id(entity.getId())
                .storeId(entity.getStore() != null ? entity.getStore().getId() : null)
                .storeName(entity.getStore() != null ? entity.getStore().getName() : null)
                .name(entity.getName())
                .stockPrice(entity.getStockPrice())
                .unitCapacity(entity.getUnitCapacity())
                .stockQuantity(entity.getStockQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}