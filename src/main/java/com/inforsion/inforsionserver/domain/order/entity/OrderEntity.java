package com.inforsion.inforsionserver.domain.order.entity;

import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.global.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "orders", indexes =  {
        @Index(name = "idx_orders_created_at", columnList = "created_at"),
        @Index(name = "idx_orders_status", columnList = "order_status")
})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id; // 주문 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private ProductEntity menu; // 메뉴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction; // 거래

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 주문량

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // 단가

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice; // 총 가격

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 주문 상태 (취소, 완료, 진행)
}
