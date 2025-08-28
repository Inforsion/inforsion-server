package com.inforsion.inforsionserver.domain.inventory.repository;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.QInventoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;

    /**
     * 재고 정보 등록
     *
     * @param storeId 가게 ID
     * @param threshold 재고 부족 임계값
     *
     */

    /**
     * 전체 재고 조회
     * 
     * @param storeId 가게 ID
     * @param threshold 재고 부족 임계값
     *
     */
    public Page<InventoryEntity> findAllInven(
            
    ){

    }


}