package com.inforsion.inforsionserver.domain.order.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "Orders", indexes =  {
        @Index(name = "idx_orders_created_at", columnList = "created_at"),
        @Index(name = "idx_orders_status", columnList = "order_status")
})
public class OrderEntity {

    private LocalDateTime createdAt;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id; // 주문 id

    @Column(name = "subtotal_amount")
    private BigDecimal subtotal_amount; // 세금 전 총액

    @Column(name = "total_amount")
    private BigDecimal total_amount; // 총액

    @Column(name = "order_name")
    private String name; // 주문 이름

    @Column(name = "order_quantity")
    private Integer quantity; // 주문량

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // 거래 방법

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 (취소, 완료, 진행)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity storeEntity;
}
