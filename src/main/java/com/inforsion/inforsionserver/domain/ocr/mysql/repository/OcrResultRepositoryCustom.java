package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.global.enums.MatchMethod;
import com.inforsion.inforsionserver.global.enums.MatchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OcrResultRepositoryCustom {
    
    Page<OcrResultEntity> findOcrResultsByStoreIdWithPaging(Integer storeId, Pageable pageable);
    
    List<OcrResultEntity> findOcrResultsByStoreAndDateRange(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<OcrResultEntity> findOcrResultsByMatchTypeAndTargetId(MatchType matchType, Integer targetId);
    
    List<OcrResultEntity> findOcrResultsByStoreAndMatchMethod(Integer storeId, MatchMethod matchMethod);
    
    Page<OcrResultEntity> findOcrResultsByRawDataIdWithPaging(Integer rawDataId, Pageable pageable);
    
    Long countOcrResultsByStoreAndMatchType(Integer storeId, MatchType matchType);
}