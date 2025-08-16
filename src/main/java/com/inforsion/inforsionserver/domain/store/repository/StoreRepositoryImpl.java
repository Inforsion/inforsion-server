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
    
    /**
     * 특정 지역의 활성 가게 페이징 조회
     * 
     * 지역명으로 가게를 검색하고 페이징 처리하여 반환합니다.
     * 고객이 특정 지역의 가게를 찾을 때 사용됩니다.
     * 성능 최적화를 위해 별도의 count 쿼리를 사용합니다.
     * 
     * @param location 검색할 지역명
     * @param pageable 페이징 정보
     * @return 페이징된 가게 목록
     */
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
    
    /**
     * 상품을 보유한 활성 가게 목록 조회
     * 
     * Store와 Product를 조인하여 실제 상품이 등록된 가게만 조회합니다.
     * distinct를 사용하여 중복된 가게를 제거합니다.
     * 고객에게 상품이 있는 가게만 표시할 때 사용됩니다.
     * 
     * @return 상품을 보유한 가게 목록
     */
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
    
    /**
     * 특정 사용자의 활성 가게 개수 조회
     * 
     * 사용자 ID로 해당 사용자가 운영하는 활성 상태 가게의 개수를 집계합니다.
     * 사용자 대시보드에서 운영 중인 가게 수를 표시할 때 사용됩니다.
     * 
     * @param userId 사용자 ID
     * @return 활성 가게 개수
     */
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
    
    /**
     * 키워드로 가게 검색
     * 
     * 가게명, 설명, 위치 중 하나라도 키워드를 포함하는 활성 가게를 검색합니다.
     * 대소문자 구분 없이 검색하여 사용자 편의성을 향상시킵니다.
     * 통합 검색 기능에서 사용됩니다.
     * 
     * @param keyword 검색 키워드
     * @return 키워드와 일치하는 가게 목록
     */
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
    
    /**
     * 지역명 검색 조건 생성
     * 
     * null 체크를 통해 안전한 동적 쿼리 조건을 생성합니다.
     * BooleanExpression을 반환하여 where절에서 조건을 조합할 수 있습니다.
     * 
     * @param location 검색할 지역명
     * @return 지역명 검색 조건 (null이면 조건 무시)
     */
    private BooleanExpression locationContains(String location) {
        return location != null ? QStoreEntity.storeEntity.location.containsIgnoreCase(location) : null;
    }
}