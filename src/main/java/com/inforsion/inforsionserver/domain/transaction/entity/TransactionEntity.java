package com.inforsion.inforsionserver.domain.transaction.entity;

import com.inforsion.inforsionserver.domain.order.entity.OrderEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.global.enums.CostCategory;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer id; // 거래 조회를 위한 id

    @Column(name = "transaction_name")
    private String name; // 거래 이름

    @Column(name = "transaction_date")
    private LocalDateTime date; // 거래 날짜

    @Column(name = "transaction_amount")
    private BigDecimal amount; // 거래 금액

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // 거래 방법

    @Enumerated(EnumType.STRING)
    private TransactionType type; // 거래 유형

    @Enumerated(EnumType.STRING)
    private TransactionCategory transactionCategory; // 매출 관련 카테고리

    @Enumerated(EnumType.STRING)
    private CostCategory costCategory; // 원가 관련 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store; // 가게 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order; // 주문 id

}
