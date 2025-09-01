package com.inforsion.inforsionserver.domain.transaction.dto.response;

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
public class TransactionResponseDto {
    private Integer id; // 매출 id
    private Integer storeId; // 가게 id
    private String name; // 매출 이름
    private LocalDateTime date; // 매출 날짜
    private BigDecimal amount; // 매출량
    private TransactionType type; // 매출 타입

}
