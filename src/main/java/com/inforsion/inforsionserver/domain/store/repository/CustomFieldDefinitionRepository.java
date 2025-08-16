package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.CustomFieldDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldDefinitionRepository extends JpaRepository<CustomFieldDefinitionEntity, Integer>,
CustomFieldDefinitionRepositoryCustom {

    List<CustomFieldDefinitionEntity> findByStoreIdAndIsActiveTrueOrderByDisplayOrder(Integer storeId);

    List<CustomFieldDefinitionEntity> findByStoreIdOrderByDisplayOrder(Integer storeId);
    
    boolean existsByStoreIdAndFieldName(Integer storeId, String fieldName);
}
