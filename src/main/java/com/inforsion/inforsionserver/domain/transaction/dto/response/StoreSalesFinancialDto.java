package com.inforsion.inforsionserver.domain.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StoreSalesFinancialDto {
    private Integer storeId; // 가게 id
    private String storeName; // 가게 이름
    private String period; // 기간
    private BigDecimal cardSales; // 카드 매출액
    private BigDecimal cashSales; // 현금 매출액
    private BigDecimal otherSales; // 이외 매출액
    private BigDecimal grossSales; // 총매출액
    private BigDecimal refundAmount; // 반품액
    private BigDecimal materialCost; // 제품 가격
    private BigDecimal fixedCost; // 고정 지출액
    private BigDecimal otherCost; // 이외 지출액
    private BigDecimal totalCost; // 총 지출액
    private BigDecimal taxAmount; // 세금
    private BigDecimal netProfit; // 순이익
}
