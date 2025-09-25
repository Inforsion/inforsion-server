package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.global.enums.MatchMethod;
import com.inforsion.inforsionserver.global.enums.MatchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OcrResultRepository extends JpaRepository<OcrResultEntity, Integer>, OcrResultRepositoryCustom {

    List<OcrResultEntity> findByStoreIdOrderByCreatedAtDesc(Integer storeId);

    List<OcrResultEntity> findByRawDataId(Integer rawDataId);

    List<OcrResultEntity> findByMatchType(MatchType matchType);

    List<OcrResultEntity> findByMatchMethod(MatchMethod matchMethod);

    List<OcrResultEntity> findByStoreIdAndMatchType(Integer storeId, MatchType matchType);

    List<OcrResultEntity> findByTargetIdAndMatchType(Integer targetId, MatchType matchType);

    @Query("SELECT o FROM OcrResultEntity o WHERE o.store.id = :storeId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<OcrResultEntity> findByStoreIdAndCreatedAtBetween(@Param("storeId") Integer storeId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM OcrResultEntity o WHERE o.matchType = :matchType AND o.targetId = :targetId ORDER BY o.createdAt DESC")
    List<OcrResultEntity> findByMatchTypeAndTargetIdOrderByCreatedAtDesc(@Param("matchType") MatchType matchType, 
                                                                        @Param("targetId") Integer targetId);
}