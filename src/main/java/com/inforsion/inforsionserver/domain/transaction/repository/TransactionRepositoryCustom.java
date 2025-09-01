package com.inforsion.inforsionserver.domain.transaction.repository;

import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.inforsion.inforsionserver.global.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepositoryCustom {

    // 거래 조회
    List<TransactionResponseDto> findByStoreIdDateRange(
            Integer storeId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate);

    // 거래 수정
    Long updateTransaction(Integer transactionId, TransactionRequestDto requestDto);

    // 거래 삭제
    Long deleteTransaction(Integer transactionId);

    // 매출 관리
    List<StoreSalesFinancialDto> getStoreFinancials(
            TransactionConditionDto condition, PeriodType periodType
    );

}
