package com.inforsion.inforsionserver.domain.order.dto.request;

import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderRequestDto {
    private Integer id; // 주문 id
    private Integer menuId; // 메뉴 id
    private Integer transactionId; // 거래 id
    private Integer quantity; // 주문량
    private BigDecimal unitPrice; // 단가
    private BigDecimal totalPrice; // 총 가격
    private OrderStatus orderStatus; // 주문 상태

    public static OrderRequestDto fromEntity(OrderEntity entity) {
        return OrderRequestDto.builder()
                .id(entity.getId())
                .menuId(entity.getMenu() != null ? entity.getMenu().getId() : null)
                .transactionId(entity.getTransaction() != null ? entity.getTransaction().getId() : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .orderStatus(entity.getOrderStatus())
                .build();
    }
}
