package com.inforsion.inforsionserver.domain.order.entity;

import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@Table(name = "Orders", indexes =  {
        @Index(name = "idx_orders_created_at", columnList = "created_at"),
        @Index(name = "idx_orders_status", columnList = "order_status")
})
public class OrderEntity {
    @Id
    @Column(name = "order_id")
    private Integer id; // 주문 id

    @Column(name = "order_name")
    private String name; // 주문 이름

    @Column(name = "subtotal_amount")
    private BigDecimal subtotal_amount; // 세금 전 총액

    @Column(name = "total_amount")
    private BigDecimal total_amount; // 총액

    @Column(name = "order_quantity")
    private Integer quantity; // 주문량

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // 거래 방법

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태 (취소, 완료, 진행)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private ProductEntity menu; // 메뉴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction; // 거래


    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // 단가

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice; // 총 가격

}
