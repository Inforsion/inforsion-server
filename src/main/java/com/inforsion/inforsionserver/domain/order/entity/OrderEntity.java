package com.inforsion.inforsionserver.domain.order.entity;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import jakarta.persistence.*;
import lombok.Getter;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;

import java.util.ArrayList;

@Entity
@Getter
@Table(name = "Orders", indexes =  {
        @Index(name = "idx_orders_created_at", columnList = "created_at"),
        @Index(name = "idx_orders_status", columnList = "status")
})
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subtotal_amount", precision = 12, scale = 2, nullable = false)
    private Double subtotal_amount; // 세금 할인 적용 전 총액

    @Column(name = "total_amount")
    private Double total_amount; // 총액 : 세금 포함된 값

    @Column(name = "order_name")
    private String name;

    @Column(name = "order_quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // 거래 방법

    private StoreEntity storeEntity;
}
