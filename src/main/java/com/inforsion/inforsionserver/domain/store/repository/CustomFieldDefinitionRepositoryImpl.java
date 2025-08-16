package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.store.entity.CustomFieldDefinitionEntity;
import com.inforsion.inforsionserver.domain.store.entity.QCustomFieldDefinitionEntity;
import com.inforsion.inforsionserver.global.enums.CustomFieldType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomFieldDefinitionRepositoryImpl implements CustomFieldDefinitionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCustomFieldDefinitionEntity customField = QCustomFieldDefinitionEntity.customFieldDefinitionEntity;

    @Override
    public List<CustomFieldDefinitionEntity> findByStoreIdAndFieldType(Integer storeId, CustomFieldType fieldType) {
        return queryFactory
                .selectFrom(customField)
                .where(
                    customField.store.id.eq(storeId)
                    .and(customField.fieldType.eq(fieldType))
                    .and(customField.isActive.isTrue())
                )
                .orderBy(customField.displayOrder.asc())
                .fetch();
    }

    @Override
    public List<CustomFieldDefinitionEntity> findRequiredFieldsByStoreId(Integer storeId) {
        return queryFactory
                .selectFrom(customField)
                .where(
                    customField.store.id.eq(storeId)
                    .and(customField.isRequired.isTrue())
                    .and(customField.isActive.isTrue())
                )
                .orderBy(customField.displayOrder.asc())
                .fetch();
    }

    @Override
    public void updateDisplayOrders(Integer storeId, List<Integer> fieldIds) {
        for (int i = 0; i < fieldIds.size(); i++) {
            queryFactory
                    .update(customField)
                    .set(customField.displayOrder, i + 1)
                    .where(
                        customField.id.eq(fieldIds.get(i))
                        .and(customField.store.id.eq(storeId))
                    )
                    .execute();
        }
    }
}