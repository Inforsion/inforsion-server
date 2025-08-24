package com.inforsion.inforsionserver.domain.transaction.dto.response;

import com.inforsion.inforsionserver.global.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Integer id;
    private Integer storeId; // 가게 id
    private String name; // 거래 이름
    private LocalDateTime date; // 거래 날짜
    private BigDecimal amount; // 거래 금액
    private TransactionType type; // 거래 유형

}
