package com.inforsion.inforsionserver.domain.store.repository;

import com.inforsion.inforsionserver.domain.product.entity.QProductEntity;
import com.inforsion.inforsionserver.domain.store.entity.QStoreEntity;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<StoreEntity> findActiveStoresByLocation(String location, Pageable pageable) {
        QStoreEntity store = QStoreEntity.storeEntity;
        
        List<StoreEntity> content = queryFactory
                .selectFrom(store)
                .where(store.isActive.eq(true)
                        .and(locationContains(location)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(store.count())
                .from(store)
                .where(store.isActive.eq(true)
                        .and(locationContains(location)));
        
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
    
    @Override
    public List<StoreEntity> findStoresWithProducts() {
        QStoreEntity store = QStoreEntity.storeEntity;
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(store)
                .join(store.products, product)
                .where(store.isActive.eq(true))
                .distinct()
                .fetch();
    }
    
    @Override
    public Long countActiveStoresByUserId(Integer userId) {
        QStoreEntity store = QStoreEntity.storeEntity;
        
        return queryFactory
                .select(store.count())
                .from(store)
                .where(store.user.id.eq(userId)
                        .and(store.isActive.eq(true)))
                .fetchOne();
    }
    
    @Override
    public List<StoreEntity> searchStoresByKeyword(String keyword) {
        QStoreEntity store = QStoreEntity.storeEntity;
        
        return queryFactory
                .selectFrom(store)
                .where(store.isActive.eq(true)
                        .and(store.name.containsIgnoreCase(keyword)
                                .or(store.description.containsIgnoreCase(keyword))
                                .or(store.location.containsIgnoreCase(keyword))))
                .fetch();
    }
    
    private BooleanExpression locationContains(String location) {
        return location != null ? QStoreEntity.storeEntity.location.containsIgnoreCase(location) : null;
    }
}