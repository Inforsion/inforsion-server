package com.inforsion.inforsionserver.domain.product.repository;

import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepositoryCustom {
    
    Page<ProductEntity> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    List<ProductEntity> findPopularProducts(Integer storeId, int limit);
    
    List<ProductEntity> searchProductsByKeyword(String keyword);
    
    List<ProductEntity> findProductsByStoreAndCategory(Integer storeId, String category);
    
    Long countInStockProductsByStoreId(Integer storeId);
}