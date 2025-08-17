package com.inforsion.inforsionserver.domain.transaction.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class SalesAnalyticsResponse {

    // 기본 정보
    private Integer storeId;
    private String storeName;
    private LocalDateTime calculatedAt;
    
    // 매출 관련
    private BigDecimal totalRevenue;        // 총 매출
    private BigDecimal cardRevenue;         // 카드 매출
    private BigDecimal cashRevenue;         // 현금 매출
    private BigDecimal bankTransferRevenue; // 계좌이체 매출
    private BigDecimal otherRevenue;        // 기타 매출
    
    // 비용 관련
    private BigDecimal totalExpense;        // 총 비용
    private BigDecimal materialCost;        // 재료비
    private BigDecimal fixedCost;           // 고정비
    private BigDecimal utilityCost;         // 공과금
    private BigDecimal laborCost;           // 인건비
    private BigDecimal taxAmount;           // 세금
    private BigDecimal refundAmount;        // 환불금액
    private BigDecimal otherExpense;        // 기타 비용
    
    // 최종 계산
    private BigDecimal netProfit;           // 순이익 (매출 - 비용)
    private BigDecimal profitMargin;        // 이익률 ((순이익 / 매출) * 100)
    
    // 카테고리별 상세 데이터
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, BigDecimal> expenseByCategory;
}