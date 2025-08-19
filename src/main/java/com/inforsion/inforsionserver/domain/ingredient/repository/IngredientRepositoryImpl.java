package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.entity.QIngredientEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.QProductEntity;
import com.inforsion.inforsionserver.domain.store.entity.QStoreEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    /**
     * 특정 가게에서 사용하는 모든 재료 조회
     * 
     * Ingredient와 Product를 조인하여 특정 가게의 모든 상품에 사용되는 재료를 조회합니다.
     * distinct를 사용하여 중복된 재료를 제거합니다.
     * 가게 재료 관리 페이지에서 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @return 해당 가게에서 사용하는 재료 목록
     */
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
    
    /**
     * 여러 상품에 공통으로 사용되는 재료 조회
     * 
     * 주어진 상품 ID 목록에 모두 포함되는 재료를 찾습니다.
     * GROUP BY와 HAVING을 사용하여 모든 상품에 공통으로 사용되는 재료만 필터링합니다.
     * 메뉴 조합 추천이나 공통 재료 관리에 사용됩니다.
     * 
     * @param productIds 상품 ID 목록
     * @return 모든 상품에 공통으로 사용되는 재료 목록
     */
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
    
    /**
     * 재고 부족 재료 조회
     * 
     * Ingredient, Inventory, Product를 조인하여 특정 가게에서 
     * 재고량이 임계값 이하인 재료들을 조회합니다.
     * 재고 알림 시스템이나 발주 관리에 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param threshold 재고 부족 임계값
     * @return 재고가 부족한 재료 목록
     */
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
    
    /**
     * 특정 상품의 재료 개수 조회
     * 
     * 하나의 상품에 사용되는 재료의 총 개수를 집계합니다.
     * 상품 복잡도 분석이나 원가 계산에 사용됩니다.
     * 
     * @param productId 상품 ID
     * @return 해당 상품에 사용되는 재료 개수
     */
    @Override
    public Long countIngredientsByProductId(Integer productId) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        
        return queryFactory
                .select(ingredient.count())
                .from(ingredient)
                .where(ingredient.product.id.eq(productId))
                .fetchOne();
    }

    /**
     * 동적 조건을 사용한 재료 검색
     * 
     * IngredientSearchRequest의 각 필드를 기반으로 동적 쿼리를 생성합니다.
     * null이 아닌 필드들만을 조건으로 사용하여 유연한 검색을 제공합니다.
     * 
     * @param request 검색 조건 DTO
     * @return 검색 조건에 맞는 재료 목록
     */
    @Override
    public List<IngredientEntity> searchIngredients(IngredientSearchRequest request) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        QProductEntity product = QProductEntity.productEntity;
        QStoreEntity store = QStoreEntity.storeEntity;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        // 재료명 부분 검색
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            builder.and(ingredient.name.containsIgnoreCase(request.getName().trim()));
        }
        
        // 상품 ID 조건
        if (request.getProductId() != null) {
            builder.and(ingredient.product.id.eq(request.getProductId()));
        }
        
        // 가게 ID 조건
        if (request.getStoreId() != null) {
            builder.and(product.store.id.eq(request.getStoreId()));
        }
        
        // 단위 조건
        if (request.getUnit() != null && !request.getUnit().trim().isEmpty()) {
            builder.and(ingredient.unit.eq(request.getUnit().trim()));
        }
        
        // 활성화 상태 조건
        if (request.getIsActive() != null) {
            builder.and(ingredient.isActive.eq(request.getIsActive()));
        }
        
        return queryFactory
                .selectFrom(ingredient)
                .leftJoin(ingredient.product, product).fetchJoin()
                .leftJoin(product.store, store).fetchJoin()
                .where(builder)
                .orderBy(ingredient.createdAt.desc())
                .fetch();
    }
}