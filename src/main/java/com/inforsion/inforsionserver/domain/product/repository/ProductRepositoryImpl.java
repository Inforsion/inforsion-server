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
    
    /**
     * 가격 범위로 상품 페이징 조회
     * 
     * 최소/최대 가격 조건으로 재고가 있는 상품을 검색하고 페이징 처리합니다.
     * 가격 오름차순으로 정렬하여 고객이 저렴한 상품부터 볼 수 있습니다.
     * 상품 필터링 기능에서 사용됩니다.
     * 
     * @param minPrice 최소 가격 (null이면 조건 무시)
     * @param maxPrice 최대 가격 (null이면 조건 무시)
     * @param pageable 페이징 정보
     * @return 페이징된 상품 목록
     */
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
    
    /**
     * 특정 가게의 인기 상품(시그니처 메뉴) 조회
     * 
     * 시그니처로 설정된 재고 보유 상품을 제한된 개수만큼 조회합니다.
     * 가게 메인 페이지에서 대표 상품을 노출할 때 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param limit 조회할 최대 개수
     * @return 인기 상품 목록
     */
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
    
    /**
     * 키워드로 상품 검색
     * 
     * 상품명, 설명, 카테고리 중 하나라도 키워드를 포함하는 재고 보유 상품을 검색합니다.
     * 대소문자 구분 없이 검색하여 사용자 편의성을 향상시킵니다.
     * 통합 검색 기능에서 사용됩니다.
     * 
     * @param keyword 검색 키워드
     * @return 키워드와 일치하는 상품 목록
     */
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
    
    /**
     * 특정 가게의 카테고리별 상품 조회
     * 
     * 가게 ID와 카테고리로 재고가 있는 상품만 조회합니다.
     * 카테고리별 상품 목록 페이지에서 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @param category 상품 카테고리
     * @return 해당 카테고리의 상품 목록
     */
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
    
    /**
     * 특정 가게의 재고 보유 상품 개수 조회
     * 
     * 가게 ID로 현재 판매 가능한 상품 개수를 집계합니다.
     * 가게 대시보드에서 판매 중인 상품 수를 표시할 때 사용됩니다.
     * 
     * @param storeId 가게 ID
     * @return 재고 보유 상품 개수
     */
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
    
    /**
     * 최소 가격 조건 생성
     * 
     * null 체크를 통해 안전한 동적 쿼리 조건을 생성합니다.
     * Greater Or Equal (>=) 조건을 생성합니다.
     * 
     * @param minPrice 최소 가격
     * @return 최소 가격 조건 (null이면 조건 무시)
     */
    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice != null ? QProductEntity.productEntity.price.goe(minPrice) : null;
    }
    
    /**
     * 최대 가격 조건 생성
     * 
     * null 체크를 통해 안전한 동적 쿼리 조건을 생성합니다.
     * Less Or Equal (<=) 조건을 생성합니다.
     * 
     * @param maxPrice 최대 가격
     * @return 최대 가격 조건 (null이면 조건 무시)
     */
    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice != null ? QProductEntity.productEntity.price.loe(maxPrice) : null;
    }
    
    /**
     * 카테고리 일치 조건 생성
     * 
     * null 체크를 통해 안전한 동적 쿼리 조건을 생성합니다.
     * 정확히 일치하는 카테고리 조건을 생성합니다.
     * 
     * @param category 상품 카테고리
     * @return 카테고리 조건 (null이면 조건 무시)
     */
    private BooleanExpression categoryEq(String category) {
        return category != null ? QProductEntity.productEntity.category.eq(category) : null;
    }
}