package com.inforsion.inforsionserver.domain.transaction.dto.request;

import com.inforsion.inforsionserver.global.enums.CostCategory;
import com.inforsion.inforsionserver.global.enums.PaymentMethod;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {
    private Integer storeId; // 가게 아이디
    private String name; // 거래 이름
    private LocalDateTime date; // 거래 날짜
    private BigDecimal amount; // 거래 금액
    private TransactionType type; // 거래 유형
    private PaymentMethod paymentMethod;
    private CostCategory costCategory;
}
