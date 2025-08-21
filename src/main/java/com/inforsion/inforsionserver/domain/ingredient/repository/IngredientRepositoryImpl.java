package com.inforsion.inforsionserver.domain.ingredient.repository;

import com.inforsion.inforsionserver.domain.ingredient.dto.request.IngredientSearchRequest;
import com.inforsion.inforsionserver.domain.ingredient.entity.IngredientEntity;
import com.inforsion.inforsionserver.domain.ingredient.entity.QIngredientEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.inforsion.inforsionserver.domain.product.entity.QProductEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;

    /**
     * 동적 조건을 사용한 재료 검색
     * 
     * IngredientSearchRequest의 각 필드를 기반으로 동적 쿼리를 생성합니다.
     * null이 아닌 필드들만을 조건으로 사용하여 유연한 검색을 제공합니다.
     * 메뉴 재료 관리에 특화된 검색 기능입니다.
     * 
     * @param request 검색 조건 DTO
     * @return 검색 조건에 맞는 재료 목록
     */
    @Override
    public List<IngredientEntity> searchIngredients(IngredientSearchRequest request) {
        QIngredientEntity ingredient = QIngredientEntity.ingredientEntity;
        QProductEntity product = QProductEntity.productEntity;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        // 상품 ID 조건
        if (request.getProductId() != null) {
            builder.and(ingredient.product.id.eq(request.getProductId()));
        }
        
        // 재고 ID 조건
        if (request.getInventoryId() != null) {
            builder.and(ingredient.inventory.id.eq(request.getInventoryId()));
        }
        
        // 단위 조건
        if (request.getUnit() != null && !request.getUnit().trim().isEmpty()) {
            builder.and(ingredient.unit.eq(request.getUnit().trim()));
        }
        
        // 활성화 상태 조건
        if (request.getIsActive() != null) {
            builder.and(ingredient.isActive.eq(request.getIsActive()));
        }
        
        QInventoryEntity inventory = QInventoryEntity.inventoryEntity;
        
        return queryFactory
                .selectFrom(ingredient)
                .leftJoin(ingredient.product, product).fetchJoin()
                .leftJoin(ingredient.inventory, inventory).fetchJoin()
                .where(builder)
                .orderBy(ingredient.createdAt.desc())
                .fetch();
    }
}