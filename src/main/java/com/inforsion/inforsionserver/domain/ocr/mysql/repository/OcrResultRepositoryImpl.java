package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.QOcrResultEntity;
import com.inforsion.inforsionserver.global.enums.MatchMethod;
import com.inforsion.inforsionserver.global.enums.MatchType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OcrResultRepositoryImpl implements OcrResultRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QOcrResultEntity qOcr = QOcrResultEntity.ocrResultEntity;

    @Override
    public Page<OcrResultEntity> findOcrResultsByStoreIdWithPaging(Integer storeId, Pageable pageable) {
        List<OcrResultEntity> results = queryFactory
                .selectFrom(qOcr)
                .where(qOcr.store.id.eq(storeId))
                .orderBy(qOcr.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qOcr.count())
                .from(qOcr)
                .where(qOcr.store.id.eq(storeId))
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    @Override
    public List<OcrResultEntity> findOcrResultsByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .selectFrom(qOcr)
                .where(qOcr.store.id.eq(storeId)
                        .and(qOcr.createdAt.between(startDate, endDate)))
                .orderBy(qOcr.createdAt.desc())
                .fetch();
    }

    @Override
    public List<OcrResultEntity> findOcrResultsByMatchTypeAndTargetId(MatchType matchType, Integer targetId) {
        return queryFactory
                .selectFrom(qOcr)
                .where(qOcr.matchType.eq(matchType)
                        .and(qOcr.targetId.eq(targetId)))
                .orderBy(qOcr.createdAt.desc())
                .fetch();
    }

    @Override
    public List<OcrResultEntity> findOcrResultsByStoreAndMatchMethod(Integer storeId, MatchMethod matchMethod) {
        return queryFactory
                .selectFrom(qOcr)
                .where(qOcr.store.id.eq(storeId)
                        .and(qOcr.matchMethod.eq(matchMethod)))
                .orderBy(qOcr.createdAt.desc())
                .fetch();
    }

    @Override
    public Page<OcrResultEntity> findOcrResultsByRawDataIdWithPaging(Integer rawDataId, Pageable pageable) {
        List<OcrResultEntity> results = queryFactory
                .selectFrom(qOcr)
                .where(qOcr.rawDataId.eq(rawDataId))
                .orderBy(qOcr.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qOcr.count())
                .from(qOcr)
                .where(qOcr.rawDataId.eq(rawDataId))
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    @Override
    public Long countOcrResultsByStoreAndMatchType(Integer storeId, MatchType matchType) {
        return queryFactory
                .select(qOcr.count())
                .from(qOcr)
                .where(qOcr.store.id.eq(storeId)
                        .and(qOcr.matchType.eq(matchType)))
                .fetchOne();
    }
}