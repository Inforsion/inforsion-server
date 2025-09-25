package com.inforsion.inforsionserver.domain.order.dto.response;

import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class OrderResponseDto {
    private Integer storeId;
    private String storeName;
    private Integer id; // 주문 id
    private BigDecimal subtotal_amount; // 세금 전 총액
    private BigDecimal total_amount; // 총액
    private String name; // 주문 이름
    private Integer quantity; // 주문량
    private PaymentMethod paymentMethod; // 거래 방법
    private OrderStatus orderStatus; // 주문 상태 (취소, 완료, 진행)


    public static OrderResponseDto fromEntity(OrderEntity entity) {
        return OrderResponseDto.builder()
                .id(entity.getId())
                .storeId(entity.getStore().getId())
                .name(entity.getName())
                .quantity(entity.getQuantity())
                .subtotal_amount(entity.getSubtotal_amount())
                .total_amount(entity.getTotal_amount())
                .paymentMethod(entity.getPaymentMethod())
                .orderStatus(entity.getOrderStatus())
                .build();

    }
}
