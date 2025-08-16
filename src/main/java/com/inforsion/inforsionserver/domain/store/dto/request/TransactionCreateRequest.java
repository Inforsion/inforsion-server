package com.inforsion.inforsionserver.domain.store.dto.request;

import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor
public class TransactionCreateRequest {

    @NotBlank(message = "거래명은 필수입니다")
    @Size(max = 255, message = "거래명은 255자 이하여야 합니다")
    private String name;

    private String description;

    @NotNull(message = "금액은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "금액은 0보다 커야 합니다")
    private BigDecimal amount;

    @NotNull(message = "거래 유형은 필수입니다")
    private TransactionType type;

    @NotNull(message = "거래 일시는 필수입니다")
    private LocalDateTime transactionDate;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    private String category;

    @NotNull(message = "가게 ID는 필수입니다")
    private Integer storeId;

    private Map<Integer, String> customFieldValues;
}