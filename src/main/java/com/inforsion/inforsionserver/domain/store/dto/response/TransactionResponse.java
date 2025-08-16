package com.inforsion.inforsionserver.domain.store.dto.response;

import com.inforsion.inforsionserver.global.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TransactionResponse {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime transactionDate;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CustomFieldValueResponse> customFieldValues;
}