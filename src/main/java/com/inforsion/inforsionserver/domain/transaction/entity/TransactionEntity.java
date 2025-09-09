package com.inforsion.inforsionserver.domain.transaction.entity;

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
@Table(name = "transactions")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store; // 가게 id

    @Column(name = "name")
    private String name; // 거래 이름

    @Column(name = "date")
    private LocalDateTime date; // 거래 날짜

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // 거래 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // 결제 수단

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // 거래 유형

    @Column(name = "transaction_memo", columnDefinition = "TEXT")
    private String transactionMemo; // 거래 메모

    @Enumerated(EnumType.STRING)
    private TransactionCategory transactionCategory; // 매출 관련 카테고리

    @Enumerated(EnumType.STRING)
    private CostCategory costCategory; // 원가 관련 카테고리

}
