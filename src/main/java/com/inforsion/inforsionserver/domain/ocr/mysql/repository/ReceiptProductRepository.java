package com.inforsion.inforsionserver.domain.ocr.mysql.repository;

import com.inforsion.inforsionserver.domain.ocr.mysql.entity.ReceiptProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiptProductRepository extends JpaRepository<ReceiptProductEntity, Long> {
    
    List<ReceiptProductEntity> findByOcrJobId(String ocrJobId);
    
    List<ReceiptProductEntity> findByProductNameContainingIgnoreCase(String productName);
    
    List<ReceiptProductEntity> findByProductNameNormalizedContaining(String normalizedProductName);
    
    Page<ReceiptProductEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT DISTINCT rp.productName FROM ReceiptProductEntity rp WHERE rp.productNameNormalized LIKE %:normalizedName%")
    List<String> findDistinctProductNamesByNormalizedNameContaining(@Param("normalizedName") String normalizedName);
    
    @Query("SELECT rp FROM ReceiptProductEntity rp WHERE rp.productName LIKE %:keyword% ORDER BY rp.createdAt DESC")
    List<ReceiptProductEntity> searchByProductNameKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(rp) FROM ReceiptProductEntity rp WHERE rp.productNameNormalized = :normalizedName")
    long countByNormalizedProductName(@Param("normalizedName") String normalizedName);
    
    boolean existsByOcrJobIdAndProductNameNormalized(String ocrJobId, String productNameNormalized);
}