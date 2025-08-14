package com.inforsion.inforsionserver.domain.product.repository;

import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.product.entity.QProductEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<ProductEntity> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        QProductEntity product = QProductEntity.productEntity;
        
        List<ProductEntity> content = queryFactory
                .selectFrom(product)
                .where(product.inStock.eq(true)
                        .and(priceGoe(minPrice))
                        .and(priceLoe(maxPrice)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.price.asc())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(product.inStock.eq(true)
                        .and(priceGoe(minPrice))
                        .and(priceLoe(maxPrice)));
        
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
    
    @Override
    public List<ProductEntity> findPopularProducts(Integer storeId, int limit) {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(product)
                .where(product.store.id.eq(storeId)
                        .and(product.inStock.eq(true))
                        .and(product.isSignature.eq(true)))
                .limit(limit)
                .fetch();
    }
    
    @Override
    public List<ProductEntity> searchProductsByKeyword(String keyword) {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(product)
                .where(product.inStock.eq(true)
                        .and(product.name.containsIgnoreCase(keyword)
                                .or(product.description.containsIgnoreCase(keyword))
                                .or(product.category.containsIgnoreCase(keyword))))
                .fetch();
    }
    
    @Override
    public List<ProductEntity> findProductsByStoreAndCategory(Integer storeId, String category) {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .selectFrom(product)
                .where(product.store.id.eq(storeId)
                        .and(product.inStock.eq(true))
                        .and(categoryEq(category)))
                .fetch();
    }
    
    @Override
    public Long countInStockProductsByStoreId(Integer storeId) {
        QProductEntity product = QProductEntity.productEntity;
        
        return queryFactory
                .select(product.count())
                .from(product)
                .where(product.store.id.eq(storeId)
                        .and(product.inStock.eq(true)))
                .fetchOne();
    }
    
    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice != null ? QProductEntity.productEntity.price.goe(minPrice) : null;
    }
    
    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice != null ? QProductEntity.productEntity.price.loe(maxPrice) : null;
    }
    
    private BooleanExpression categoryEq(String category) {
        return category != null ? QProductEntity.productEntity.category.eq(category) : null;
    }
}