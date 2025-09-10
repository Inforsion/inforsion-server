package com.inforsion.inforsionserver.domain.recipes.Dto.response;

import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.security.Timestamp;

@Data
@Builder
public class RecipesResponseDto {
    private Integer storeId;
    private String storeName;
    private Integer id;
    private BigDecimal amountPerMenu;
    private String name;
    private String unit;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updateAt;

    public static RecipesResponseDto fromEntity(RecipesEntity entity) {
        return RecipesResponseDto.builder()
                .id(entity.getId())
                .amountPerMenu(entity.getAmountPerMenu())
                .unit(entity.getUnit())
                .build();

    }
}
