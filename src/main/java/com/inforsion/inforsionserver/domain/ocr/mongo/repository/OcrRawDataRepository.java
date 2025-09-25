package com.inforsion.inforsionserver.domain.ocr.mongo.repository;

import com.inforsion.inforsionserver.domain.ocr.mongo.entity.OcrRawDataEntity;
import com.inforsion.inforsionserver.global.enums.DocumentType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OcrRawDataRepository extends MongoRepository<OcrRawDataEntity, String> {

    List<OcrRawDataEntity> findByStoreIdOrderByCreatedAtDesc(Integer storeId);

    List<OcrRawDataEntity> findByDocumentType(DocumentType documentType);

    List<OcrRawDataEntity> findByStoreIdAndDocumentType(Integer storeId, DocumentType documentType);

    Optional<OcrRawDataEntity> findByRawDataId(Integer rawDataId);

    List<OcrRawDataEntity> findBySupplierName(String supplierName);

    @Query("{ 'storeId': ?0, 'documentDate': { $gte: ?1, $lte: ?2 } }")
    List<OcrRawDataEntity> findByStoreIdAndDocumentDateBetween(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'storeId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<OcrRawDataEntity> findByStoreIdAndCreatedAtBetween(Integer storeId, LocalDateTime startDate, LocalDateTime endDate);
}