package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.entity.QIngredientEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.QProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<IngredientEntity> findIngredientsByStoreId(Integer storeId) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(ingredient)
                .join(ingredient.product, product)
                .where(product.store.id.eq(storeId))
                .distinct()
                .fetch();
    }
    
    @Override
    public List<IngredientEntity> findCommonIngredients(List<Integer> productIds) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        
        return queryFactory
                .selectFrom(ingredient)
                .where(ingredient.product.id.in(productIds))
                .groupBy(ingredient.name)
                .having(ingredient.product.id.countDistinct().eq((long) productIds.size()))
                .fetch();
    }
    
    @Override
    public List<IngredientEntity> findIngredientsWithLowStock(Integer storeId, Double threshold) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(ingredient)
                .join(ingredient.inventories, inventory)
                .join(ingredient.product, product)
                .where(product.store.id.eq(storeId)
                        .and(inventory.quantity.loe(BigDecimal.valueOf(threshold))))
                .distinct()
                .fetch();
    }
    
    @Override
    public Long countIngredientsByProductId(Integer productId) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        
        return queryFactory
                .select(ingredient.count())
                .from(ingredient)
                .where(ingredient.product.id.eq(productId))
                .fetchOne();
    }
}