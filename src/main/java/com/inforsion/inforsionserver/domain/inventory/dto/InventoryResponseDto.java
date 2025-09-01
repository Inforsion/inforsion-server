package com.inforsion.inforsionserver.domain.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryResponseDto {
    private Integer id;

    private String name; // 재료명

    private BigDecimal currentStock; // 현재 재료량

    private String unit; // 단위 (g, ml, 개 등)

    private BigDecimal unitCost; // 단위 당 가격

    private LocalDateTime expiryDate; // 유통기한

    private LocalDateTime lastRestockedDate; // 마지막 입고일

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime updatedAt; // 수정일

    private Integer storeId; // 가게 id
}
