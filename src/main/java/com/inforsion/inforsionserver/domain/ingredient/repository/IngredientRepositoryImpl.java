package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.entity.QIngredientEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<IngredientEntity> findByStoreIdAndNameAndIsActive(Integer storeId, String name, Boolean isActive) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;

        IngredientEntity result = queryFactory
                .selectFrom(ingredient)
                .where(
                        ingredient.store.id.eq(storeId),
                        ingredient.name.eq(name),
                        ingredient.isActive.eq(isActive)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}