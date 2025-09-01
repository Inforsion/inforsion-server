package com.inforsion.inforsionserver.domain.ocr.mongo.repository;

import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OcrResultRepository extends MongoRepository<OcrResultEntity, String> {
    
    List<OcrResultEntity> findByOriginalFileName(String originalFileName);
    
    List<OcrResultEntity> findByOcrEngine(String ocrEngine);
    
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<OcrResultEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<OcrResultEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Optional<OcrResultEntity> findFirstByOriginalFileNameOrderByCreatedAtDesc(String originalFileName);
}