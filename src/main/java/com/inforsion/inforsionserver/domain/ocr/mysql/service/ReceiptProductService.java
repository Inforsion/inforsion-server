package com.inforsion.inforsionserver.domain.ocr.mysql.service;

import com.inforsion.inforsionserver.domain.ocr.dto.ReceiptItem;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.ReceiptProductEntity;
import com.inforsion.inforsionserver.domain.ocr.mysql.repository.ReceiptProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptProductService {
    
    private final ReceiptProductRepository receiptProductRepository;
    
    @Transactional
    public List<ReceiptProductEntity> saveReceiptProducts(List<ReceiptItem> receiptItems, String ocrJobId) {
        List<ReceiptProductEntity> entities = receiptItems.stream()
                .filter(item -> item.getProductName() != null && !item.getProductName().trim().isEmpty())
                .map(item -> convertToEntity(item, ocrJobId))
                .filter(entity -> !isDuplicate(entity))
                .collect(Collectors.toList());
        
        List<ReceiptProductEntity> savedEntities = receiptProductRepository.saveAll(entities);
        log.info("영수증 제품 {}개가 MySQL에 저장되었습니다. OCR Job ID: {}", savedEntities.size(), ocrJobId);
        
        return savedEntities;
    }
    
    @Transactional
    public ReceiptProductEntity saveReceiptProduct(ReceiptItem receiptItem, String ocrJobId) {
        if (receiptItem.getProductName() == null || receiptItem.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("제품명이 비어있습니다.");
        }
        
        ReceiptProductEntity entity = convertToEntity(receiptItem, ocrJobId);
        
        if (isDuplicate(entity)) {
            log.warn("중복된 제품이 감지되어 저장하지 않습니다. OCR Job ID: {}, 제품명: {}", ocrJobId, entity.getProductName());
            return null;
        }
        
        ReceiptProductEntity savedEntity = receiptProductRepository.save(entity);
        log.info("영수증 제품이 MySQL에 저장되었습니다. ID: {}, 제품명: {}", savedEntity.getId(), savedEntity.getProductName());
        
        return savedEntity;
    }
    
    @Transactional(readOnly = true)
    public List<ReceiptProductEntity> findByOcrJobId(String ocrJobId) {
        return receiptProductRepository.findByOcrJobId(ocrJobId);
    }
    
    @Transactional(readOnly = true)
    public List<ReceiptProductEntity> searchByProductName(String productName) {
        String normalizedName = normalizeProductName(productName);
        return receiptProductRepository.findByProductNameNormalizedContaining(normalizedName);
    }
    
    @Transactional(readOnly = true)
    public List<String> findDistinctProductNames(String keyword) {
        String normalizedKeyword = normalizeProductName(keyword);
        return receiptProductRepository.findDistinctProductNamesByNormalizedNameContaining(normalizedKeyword);
    }
    
    @Transactional(readOnly = true)
    public Page<ReceiptProductEntity> findProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return receiptProductRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<ReceiptProductEntity> searchByKeyword(String keyword) {
        return receiptProductRepository.searchByProductNameKeyword(keyword);
    }
    
    @Transactional(readOnly = true)
    public long countByProductName(String productName) {
        String normalizedName = normalizeProductName(productName);
        return receiptProductRepository.countByNormalizedProductName(normalizedName);
    }
    
    private ReceiptProductEntity convertToEntity(ReceiptItem receiptItem, String ocrJobId) {
        return ReceiptProductEntity.builder()
                .productName(receiptItem.getProductName())
                .quantity(receiptItem.getQuantity())
                .unitPrice(receiptItem.getUnitPrice())
                .totalPrice(receiptItem.getTotalPrice())
                .originalText(receiptItem.getOriginalText())
                .ocrJobId(ocrJobId)
                .build();
    }
    
    private boolean isDuplicate(ReceiptProductEntity entity) {
        return receiptProductRepository.existsByOcrJobIdAndProductNameNormalized(
                entity.getOcrJobId(), 
                normalizeProductName(entity.getProductName())
        );
    }
    
    private String normalizeProductName(String productName) {
        if (productName == null) return null;
        return productName.toLowerCase().replaceAll("\\s+", "");
    }
}