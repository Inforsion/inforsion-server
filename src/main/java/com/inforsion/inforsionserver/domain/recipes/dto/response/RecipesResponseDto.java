package com.inforsion.inforsionserver.domain.recipes.dto.response;

import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecipesResponseDto {

    private final Integer id;
    private final Integer storeId;
    private final String storeName;
    private final Integer menuId;
    private final String menuName;
    private final Integer inventoryId;
    private final String inventoryName;
    private final String name;
    private final BigDecimal amountPerMenu;
    private final String unit;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static RecipesResponseDto fromEntity(RecipesEntity entity) {
        return RecipesResponseDto.builder()
                .id(entity.getId())
                .storeId(entity.getStore() != null ? entity.getStore().getId() : null)
                .storeName(entity.getStore() != null ? entity.getStore().getName() : null)
                .menuId(entity.getMenu() != null ? entity.getMenu().getId() : null)
                .menuName(entity.getMenu() != null ? entity.getMenu().getName() : null)
                .inventoryId(entity.getInventory() != null ? entity.getInventory().getId() : null)
                .inventoryName(entity.getInventory() != null ? entity.getInventory().getName() : null)
                .name(entity.getName())
                .amountPerMenu(entity.getAmountPerMenu())
                .unit(entity.getUnit())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
