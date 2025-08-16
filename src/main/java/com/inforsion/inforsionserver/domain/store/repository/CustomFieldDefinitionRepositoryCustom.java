package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.CustomFieldDefinitionEntity;
import com.inforsion.inforsionserver.global.enums.CustomFieldType;

import java.util.List;

public interface CustomFieldDefinitionRepositoryCustom {
    
    List<CustomFieldDefinitionEntity> findByStoreIdAndFieldType(Integer storeId, CustomFieldType fieldType);
    
    List<CustomFieldDefinitionEntity> findRequiredFieldsByStoreId(Integer storeId);
    
    void updateDisplayOrders(Integer storeId, List<Integer> fieldIds);
}