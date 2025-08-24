package com.inforsion.inforsionserver.domain.transaction.service;

import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.dto.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.dto.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.domain.transaction.repository.TransactionRepository;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StoreRepository storeRepository;

    // 거래 생성
    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto requestDto) {
        var store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다. id=" + requestDto.getStoreId()));

        TransactionEntity entity = TransactionEntity.builder()
                .name(requestDto.getName())
                .date(requestDto.getDate())
                .amount(requestDto.getAmount())
                .type(requestDto.getType())
                .paymentMethod(requestDto.getPaymentMethod())
                .costCategory(requestDto.getCostCategory())
                .store(store)
                .build();

        TransactionEntity saved = transactionRepository.save(entity);

        return new TransactionResponseDto(
                saved.getId(),
                saved.getStore().getId(),
                saved.getStore().getName(),
                saved.getDate(),
                saved.getAmount(),
                saved.getType()
        );
    }


    // 거래 조회
    public List<TransactionResponseDto> getTransaction(
            Integer storeId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    ){
        return transactionRepository.findByStoreIdDateRange(storeId, transactionType, startDate, endDate);
    }


    // 거래 삭제
    @Transactional
    public void deleteTransaction(Integer transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("거래 내역을 찾을 수 없습니다.");
        }
        transactionRepository.deleteById(transactionId);
    }

    // 거래 수정
    @Transactional
    public TransactionResponseDto updateTransaction(Integer id, TransactionRequestDto requestDto) {
        Long updated = transactionRepository.updateTransaction(id, requestDto);
        if (updated == 0) {
            throw new IllegalArgumentException("거래 내역을 찾을 수 없습니다.");
        }
        return null;
    }

    @Transactional
    public List<StoreSalesFinancialDto> findStoreSalesFinancial(TransactionConditionDto condition, PeriodType period){
        TransactionService repo = null;
        return repo.findStoreSalesFinancial(condition, period);
    }
}
