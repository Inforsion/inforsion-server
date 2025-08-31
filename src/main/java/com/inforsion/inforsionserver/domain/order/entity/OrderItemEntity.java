package com.inforsion.inforsionserver.domain.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_items_id")
    private Integer id;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "order_item_quantity", nullable = false)
    private Integer quantity;

    @Column(name = "line_total", nullable = false)
    private Double lineTotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;
}
