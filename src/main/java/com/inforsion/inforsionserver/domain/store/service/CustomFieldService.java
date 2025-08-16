package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.request.CustomFieldCreateRequest;
import com.inforsion.inforsionserver.domain.store.dto.request.CustomFieldUpdateRequest;
import com.inforsion.inforsionserver.domain.store.dto.response.CustomFieldResponse;
import com.inforsion.inforsionserver.domain.store.entity.CustomFieldDefinitionEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.CustomFieldDefinitionRepository;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
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
public class CustomFieldService {

    private final CustomFieldDefinitionRepository customFieldRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public CustomFieldResponse createCustomField(Integer storeId, CustomFieldCreateRequest request) {
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (customFieldRepository.existsByStoreIdAndFieldName(storeId, request.getFieldName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_FIELD_NAME);
        }

        CustomFieldDefinitionEntity customField = CustomFieldDefinitionEntity.builder()
                .fieldName(request.getFieldName())
                .description(request.getDescription())
                .fieldType(request.getFieldType())
                .isRequired(request.getIsRequired())
                .displayOrder(request.getDisplayOrder())
                .defaultValue(request.getDefaultValue())
                .validationRules(request.getValidationRules())
                .store(store)
                .build();

        CustomFieldDefinitionEntity savedField = customFieldRepository.save(customField);
        return convertToResponse(savedField);
    }

    @Transactional
    public CustomFieldResponse updateCustomField(Integer storeId, Integer fieldId, CustomFieldUpdateRequest request) {
        CustomFieldDefinitionEntity customField = customFieldRepository.findById(fieldId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOM_FIELD_NOT_FOUND));

        if (!customField.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (request.getFieldName() != null) {
            customField = CustomFieldDefinitionEntity.builder()
                    .id(customField.getId())
                    .fieldName(request.getFieldName())
                    .description(request.getDescription() != null ? request.getDescription() : customField.getDescription())
                    .fieldType(customField.getFieldType())
                    .isRequired(request.getIsRequired() != null ? request.getIsRequired() : customField.getIsRequired())
                    .isActive(request.getIsActive() != null ? request.getIsActive() : customField.getIsActive())
                    .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : customField.getDisplayOrder())
                    .defaultValue(request.getDefaultValue() != null ? request.getDefaultValue() : customField.getDefaultValue())
                    .validationRules(request.getValidationRules() != null ? request.getValidationRules() : customField.getValidationRules())
                    .store(customField.getStore())
                    .createdAt(customField.getCreatedAt())
                    .build();
        }

        CustomFieldDefinitionEntity updatedField = customFieldRepository.save(customField);
        return convertToResponse(updatedField);
    }

    public List<CustomFieldResponse> getCustomFieldsByStore(Integer storeId) {
        List<CustomFieldDefinitionEntity> customFields = customFieldRepository.findByStoreIdAndIsActiveTrueOrderByDisplayOrder(storeId);
        return customFields.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CustomFieldResponse getCustomField(Integer storeId, Integer fieldId) {
        CustomFieldDefinitionEntity customField = customFieldRepository.findById(fieldId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOM_FIELD_NOT_FOUND));

        if (!customField.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return convertToResponse(customField);
    }

    @Transactional
    public void deleteCustomField(Integer storeId, Integer fieldId) {
        CustomFieldDefinitionEntity customField = customFieldRepository.findById(fieldId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOM_FIELD_NOT_FOUND));

        if (!customField.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        customFieldRepository.delete(customField);
    }

    private CustomFieldResponse convertToResponse(CustomFieldDefinitionEntity entity) {
        return CustomFieldResponse.builder()
                .id(entity.getId())
                .fieldName(entity.getFieldName())
                .description(entity.getDescription())
                .fieldType(entity.getFieldType())
                .isRequired(entity.getIsRequired())
                .isActive(entity.getIsActive())
                .displayOrder(entity.getDisplayOrder())
                .defaultValue(entity.getDefaultValue())
                .validationRules(entity.getValidationRules())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}