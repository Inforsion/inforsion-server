package com.inforsion.inforsionserver.domain.recipes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipesRequestDto {

    @NotNull
    private Integer storeId;

    @NotNull
    private Integer menuId;

    @NotNull
    private Integer inventoryId;

    private String name;

    @NotNull
    private BigDecimal amountPerMenu;

    @NotBlank
    private String unit;

    private Boolean isActive;
}
