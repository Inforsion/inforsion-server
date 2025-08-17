package com.inforsion.inforsionserver.domain.transaction.service;

import com.inforsion.inforsionserver.domain.transaction.dto.request.TransactionCreateRequest;
import com.inforsion.inforsionserver.domain.transaction.dto.response.TransactionResponse;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.transaction.entity.StoreTransactionEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.transaction.repository.StoreTransactionRepository;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreTransactionService {

    private final StoreTransactionRepository transactionRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        // 카테고리와 타입 일치 검증
        if (!request.getCategory().getTransactionType().equals(request.getType())) {
            throw new BusinessException(ErrorCode.INVALID_TRANSACTION_CATEGORY);
        }

        StoreTransactionEntity transaction = StoreTransactionEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .store(store)
                .build();

        StoreTransactionEntity savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }

    public TransactionResponse getTransaction(Integer storeId, Integer transactionId) {
        StoreTransactionEntity transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!transaction.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return convertToResponse(transaction);
    }

    public List<TransactionResponse> getTransactionsByStore(Integer storeId) {
        List<StoreTransactionEntity> transactions = transactionRepository.findByStoreIdOrderByTransactionDateDesc(storeId);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTransaction(Integer storeId, Integer transactionId) {
        StoreTransactionEntity transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!transaction.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse convertToResponse(StoreTransactionEntity entity) {
        return TransactionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .type(entity.getType())
                .category(entity.getCategory())
                .categoryDescription(entity.getCategory().getDescription())
                .transactionDate(entity.getTransactionDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}