package com.inforsion.inforsionserver.domain.transaction.service;

import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.dto.response.StoreSalesFinancialDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionConditionDto;
import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionRequestDto;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponseDto;
import com.inforsion.inforsionserver.domain.transaction.entity.TransactionEntity;
import com.inforsion.inforsionserver.domain.transaction.repository.TransactionRepository;
import com.inforsion.inforsionserver.global.enums.PeriodType;
import com.inforsion.inforsionserver.global.enums.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StoreRepository storeRepository;

    /**
     * 거래 생성
     */
    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto requestDto) {
        var store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다. id=" + requestDto.getStoreId()));

        TransactionEntity entity = TransactionEntity.builder()
                .name(requestDto.getName())
                .date(requestDto.getDate())
                .amount(requestDto.getAmount())
                .transactionType(requestDto.getType())
                .paymentMethod(requestDto.getPaymentMethod())
                .costCategory(requestDto.getCostCategory())
                .store(store)
                .build();

        TransactionEntity saved = transactionRepository.save(entity);

        return toResponseDto(saved);
    }


    /**
     * 거래 조회
     */
    public List<TransactionResponseDto> getTransaction(
            Integer storeId,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    ){
        return transactionRepository.findByStoreIdDateRange(storeId, transactionType, startDate, endDate);
    }

    /**
     * 거래 수정
     */
    @Transactional
    public TransactionResponseDto updateTransaction(Integer id, TransactionRequestDto requestDto) {
        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래 내역을 찾을 수 없습니다." + id));

        entity.setName(requestDto.getName());
        entity.setDate(requestDto.getDate());
        entity.setAmount(requestDto.getAmount());
        entity.setTransactionType(requestDto.getType());
        entity.setPaymentMethod(requestDto.getPaymentMethod());
        entity.setCostCategory(requestDto.getCostCategory());

        TransactionEntity updated = transactionRepository.save(entity);
        return toResponseDto(updated);

    }

    /**
     * 거래 삭제
     */
    @Transactional
    public void deleteTransaction(Integer transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("거래 내역을 찾을 수 없습니다.");
        }
        transactionRepository.deleteById(transactionId);
    }

    /**
     * 매출 조회
     * 기간을 받아 기간 내의 매출을 조회합니다.
     */
    @Transactional
    public List<StoreSalesFinancialDto> getStoreFinancials(TransactionConditionDto condition, PeriodType periodType){
        return transactionRepository.getStoreFinancials(condition, periodType);
    }

    private TransactionResponseDto toResponseDto(TransactionEntity entity) {
        return new TransactionResponseDto(
                entity.getId(),
                entity.getStore().getId(),
                entity.getStore().getName(),
                entity.getDate(),
                entity.getAmount(),
                entity.getTransactionType()
        );
    }
}
