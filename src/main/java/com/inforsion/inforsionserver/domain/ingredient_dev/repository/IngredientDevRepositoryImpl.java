package com.inforsion.inforsionserver.domain.ingredient_dev.repository;

import com.inforsion.inforsionserver.domain.ingredient_dev.entity.IngredientDevEntity;
import com.inforsion.inforsionserver.domain.ingredient_dev.entity.QIngredientDevEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IngredientDevRepositoryImpl implements IngredientDevRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByName(String name) {
        QIngredientDevEntity ingredient = QIngredientDevEntity.ingredientDevEntity;

        Integer foundId = queryFactory
                .select(ingredient.id)
                .from(ingredient)
                .where(ingredient.name.eq(name))
                .fetchFirst();

        return foundId != null;
    }
}
