package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<InventoryEntity> findLowStockItems(Integer storeId, BigDecimal threshold) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.quantity.loe(threshold)))
                .fetch();
    }
    
    @Override
    public List<InventoryEntity> findExpiringItems(Integer storeId, LocalDateTime beforeDate) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.expiryDate.isNotNull())
                        .and(inventory.expiryDate.before(beforeDate)))
                .orderBy(inventory.expiryDate.asc())
                .fetch();
    }
    
    @Override
    public BigDecimal getTotalQuantityByIngredientAndStore(Integer ingredientId, Integer storeId) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        BigDecimal totalQuantity = queryFactory
                .select(inventory.quantity.sum())
                .from(inventory)
                .where(inventory.ingredient.id.eq(ingredientId)
                        .and(inventory.store.id.eq(storeId)))
                .fetchOne();
        
        return totalQuantity != null ? totalQuantity : BigDecimal.ZERO;
    }
    
    @Override
    public List<InventoryEntity> findInventoryByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.updatedAt.between(startDate, endDate)))
                .orderBy(inventory.updatedAt.desc())
                .fetch();
    }
    
    @Override
    public Long countExpiredInventoryByStoreId(Integer storeId) {
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .select(inventory.count())
                .from(inventory)
                .where(inventory.store.id.eq(storeId)
                        .and(inventory.expiryDate.isNotNull())
                        .and(inventory.expiryDate.before(LocalDateTime.now())))
                .fetchOne();
    }
}