package com.inforsion.inforsionserver.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id;

    @Column(name = "order_name")
    private String name;

    @Column(name = "order_date")
    private LocalDateTime date;

    private BigDecimal price;
}
