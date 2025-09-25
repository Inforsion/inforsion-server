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
    private Integer storeId;
    private String storeName;
    private Integer id; // 주문 id
    private BigDecimal subtotal_amount; // 세금 전 총액
    private BigDecimal total_amount; // 총액
    private String name; // 주문 이름
    private Integer quantity; // 주문량
    private PaymentMethod paymentMethod; // 거래 방법
    private OrderStatus orderStatus;

    public static OrderRequestDto fromEntity(OrderEntity entity) {
        return OrderRequestDto.builder()
                .id(entity.getId())
                .storeId(entity.getStore().getId())
                .storeName(entity.getStore().getName())
                .subtotal_amount(entity.getSubtotal_amount())
                .total_amount(entity.getTotal_amount())
                .quantity(entity.getQuantity())
                .paymentMethod(entity.getPaymentMethod())
                .orderStatus(entity.getOrderStatus())
                .build();
    }
}
