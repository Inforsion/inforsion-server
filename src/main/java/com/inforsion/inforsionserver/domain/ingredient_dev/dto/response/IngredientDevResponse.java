package com.inforsion.inforsionserver.domain.ingredient_dev.dto.response;

import com.inforsion.inforsionserver.domain.ingredient_dev.entity.IngredientDevEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class IngredientDevResponse {
    private final Integer id;
    private final String name;
    private final BigDecimal stockPrice;
    private final BigDecimal unitCapacity;
    private final Integer stockQuantity;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static IngredientDevResponse from(IngredientDevEntity entity) {
        return IngredientDevResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .stockPrice(entity.getStockPrice())
                .unitCapacity(entity.getUnitCapacity())
                .stockQuantity(entity.getStockQuantity())
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
