package com.inforsion.inforsionserver.domain.report.dto;


import com.inforsion.inforsionserver.domain.report.entity.ReportEntity;
import com.inforsion.inforsionserver.global.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReportDto {
    private Integer reportId;

    // 주문 관련
    private Integer orderId;
    private String productName;
    private Integer quantity;
    private OrderStatus orderStatus;

    // 거래 관련
    private Integer transactionId;
    private String transactionName;
    private PaymentMethod paymentMethod;
    private TransactionType transactionType;
    private TransactionCategory transactionCategory;
    private String costCategory;

    // 금액 관련
    private BigDecimal grossSales;
    private BigDecimal subtotalAmount;
    private BigDecimal totalAmount;

    // 날짜
    private LocalDateTime transactionDate;

    // 매장 정보
    private String storeName;

    // 변환
    public ReportDto fromEntity(ReportEntity entity) {
        return ReportDto.builder()
                .reportId(entity.getReportId())
                .orderId(entity.getOrder().getId())
                .productName(entity.getOrder().getName())
                .quantity(entity.getQuantity())
                .orderStatus(entity.getOrderStatus())

                .transactionId(entity.getTransaction().getId())
                .paymentMethod(entity.getPaymentMethod())
                .transactionType(entity.getTransactionType())
                .transactionCategory(entity.getTransactionCategory())
                .costCategory(entity.getCostCategory())

                .subtotalAmount(entity.getSubtotalAmount())
                .totalAmount(entity.getTotalAmount())
                .transactionDate(entity.getTransactionDate())

                .storeName(entity.getTransaction().getStore().getName())
                .build();
    }
}
