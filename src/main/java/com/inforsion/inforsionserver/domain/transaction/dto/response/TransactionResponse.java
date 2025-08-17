package com.inforsion.inforsionserver.domain.transaction.dto.response;

import com.inforsion.inforsionserver.global.enums.TransactionType;
import com.inforsion.inforsionserver.global.enums.TransactionCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionCategory category;
    private String categoryDescription;
    private LocalDateTime transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}