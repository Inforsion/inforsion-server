package com.inforsion.inforsionserver.domain.recipes.Dto.request;


import com.inforsion.inforsionserver.domain.recipes.Dto.response.RecipesResponseDto;
import com.inforsion.inforsionserver.domain.recipes.entity.RecipesEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.security.Timestamp;

@Getter
@Setter
@Builder
public class RecipesRequestDto {
    private Integer storeId;
    private String storeName;
    private Integer id;
    private BigDecimal amountPerMenu;
    private String name;
    private String unit;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updateAt;
}
