package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.request.TransactionCreateRequest;
import com.inforsion.inforsionserver.domain.store.dto.response.CustomFieldValueResponse;
import com.inforsion.inforsionserver.domain.store.dto.response.TransactionResponse;
import com.inforsion.inforsionserver.domain.store.entity.*;
import com.inforsion.inforsionserver.domain.store.repository.*;
import com.inforsion.inforsionserver.global.error.exception.BusinessException;
import com.inforsion.inforsionserver.global.error.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreTransactionService {

    private final StoreTransactionRepository transactionRepository;
    private final StoreRepository storeRepository;
    private final CustomFieldDefinitionRepository customFieldRepository;
    private final TransactionCustomFieldValueRepository customFieldValueRepository;

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        StoreEntity store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        validateRequiredCustomFields(request.getStoreId(), request.getCustomFieldValues());

        StoreTransactionEntity transaction = StoreTransactionEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .amount(request.getAmount())
                .type(request.getType())
                .transactionDate(request.getTransactionDate())
                .category(request.getCategory())
                .store(store)
                .build();

        StoreTransactionEntity savedTransaction = transactionRepository.save(transaction);

        if (request.getCustomFieldValues() != null && !request.getCustomFieldValues().isEmpty()) {
            saveCustomFieldValues(savedTransaction, request.getCustomFieldValues());
        }

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

        customFieldValueRepository.deleteByTransactionId(transactionId);
        transactionRepository.delete(transaction);
    }

    private void validateRequiredCustomFields(Integer storeId, Map<Integer, String> customFieldValues) {
        List<CustomFieldDefinitionEntity> requiredFields = customFieldRepository.findRequiredFieldsByStoreId(storeId);
        
        for (CustomFieldDefinitionEntity requiredField : requiredFields) {
            String value = customFieldValues != null ? customFieldValues.get(requiredField.getId()) : null;
            if (value == null || value.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.REQUIRED_CUSTOM_FIELD_MISSING);
            }
        }
    }

    private void saveCustomFieldValues(StoreTransactionEntity transaction, Map<Integer, String> customFieldValues) {
        for (Map.Entry<Integer, String> entry : customFieldValues.entrySet()) {
            Integer customFieldId = entry.getKey();
            String value = entry.getValue();

            if (value != null && !value.trim().isEmpty()) {
                CustomFieldDefinitionEntity customField = customFieldRepository.findById(customFieldId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOM_FIELD_NOT_FOUND));

                TransactionCustomFieldValueEntity fieldValue = TransactionCustomFieldValueEntity.builder()
                        .transaction(transaction)
                        .customField(customField)
                        .value(value)
                        .build();

                customFieldValueRepository.save(fieldValue);
            }
        }
    }

    private TransactionResponse convertToResponse(StoreTransactionEntity entity) {
        List<TransactionCustomFieldValueEntity> customFieldValues = 
            customFieldValueRepository.findByTransactionId(entity.getId());

        List<CustomFieldValueResponse> customFieldResponses = customFieldValues.stream()
                .map(fieldValue -> CustomFieldValueResponse.builder()
                        .customFieldId(fieldValue.getCustomField().getId())
                        .fieldName(fieldValue.getCustomField().getFieldName())
                        .fieldType(fieldValue.getCustomField().getFieldType().name())
                        .value(fieldValue.getValue())
                        .isRequired(fieldValue.getCustomField().getIsRequired())
                        .build())
                .collect(Collectors.toList());

        return TransactionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .type(entity.getType())
                .transactionDate(entity.getTransactionDate())
                .category(entity.getCategory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .customFieldValues(customFieldResponses)
                .build();
    }
}