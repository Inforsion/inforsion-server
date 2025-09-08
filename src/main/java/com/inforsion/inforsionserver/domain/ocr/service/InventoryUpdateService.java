package com.inforsion.inforsionserver.domain.ocr.service;

import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryLogEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryLogRepository;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.ocr.mysql.entity.OcrResultEntity;
import com.inforsion.inforsionserver.domain.product.entity.ProductEntity;
import com.inforsion.inforsionserver.domain.recipe.entity.RecipeEntity;
import com.inforsion.inforsionserver.domain.recipe.repository.RecipeRepository;
import com.inforsion.inforsionserver.global.enums.InventoryLogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryUpdateService {

    private final RecipeRepository recipeRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;

    /**
     * OCR 결과에 따른 재고 자동 차감
     */
    @Transactional
    public void updateInventoryFromOcr(OcrResultEntity ocrResult) {
        try {
            // OCR 결과가 메뉴 매칭인 경우에만 재고 차감
            if (isMenuMatch(ocrResult)) {
                deductInventoryForMenu(ocrResult);
            }
        } catch (Exception e) {
            log.error("재고 업데이트 중 오류 발생: OCR ID {}, 오류: {}", ocrResult.getOcrId(), e.getMessage(), e);
            throw new RuntimeException("재고 업데이트에 실패했습니다.", e);
        }
    }

    /**
     * 메뉴 매칭인지 확인
     */
    private boolean isMenuMatch(OcrResultEntity ocrResult) {
        return ocrResult.getMatchType() != null && 
               "Menu".equals(ocrResult.getMatchType().getValue());
    }

    /**
     * 메뉴에 해당하는 재료들의 재고 차감
     */
    private void deductInventoryForMenu(OcrResultEntity ocrResult) {
        Integer menuId = ocrResult.getTargetId();
        Integer quantity = ocrResult.getQuantity();
        
        // 해당 메뉴의 레시피 조회
        List<RecipeEntity> recipes = recipeRepository.findByMenuIdAndIsActive(menuId, true);
        
        if (recipes.isEmpty()) {
            log.warn("메뉴 ID {}에 대한 활성 레시피가 없습니다.", menuId);
            return;
        }
        
        // 각 레시피의 재료별로 재고 차감
        for (RecipeEntity recipe : recipes) {
            deductIngredientInventory(recipe, quantity, ocrResult);
        }
        
        log.info("메뉴 ID {} (수량: {})에 대한 재고 차감 완료", menuId, quantity);
    }

    /**
     * 개별 재료의 재고 차감
     */
    private void deductIngredientInventory(RecipeEntity recipe, Integer menuQuantity, OcrResultEntity ocrResult) {
        InventoryEntity inventory = recipe.getInventory();
        
        // 필요한 재료 수량 계산 (레시피의 1인분 * 주문 수량)
        BigDecimal requiredAmount = recipe.getAmountPerMenu().multiply(BigDecimal.valueOf(menuQuantity));
        
        // 현재 재고 확인
        BigDecimal currentStock = inventory.getCurrentStock();
        if (currentStock.compareTo(requiredAmount) < 0) {
            log.warn("재고 부족: {} (현재: {}, 필요: {})", 
                    inventory.getName(), currentStock, requiredAmount);
            // 재고 부족 알림 생성 (추후 AlertService 연동)
        }
        
        // 재고 차감
        BigDecimal newStock = currentStock.subtract(requiredAmount);
        BigDecimal beforeQuantity = inventory.getCurrentStock();
        
        inventory.setCurrentStock(newStock);
        inventoryRepository.save(inventory);
        
        // 재고 변화 로그 생성
        createInventoryLog(inventory, ocrResult, InventoryLogType.DEDUCTION, 
                          requiredAmount.negate(), beforeQuantity, newStock, 
                          "OCR 매칭을 통한 자동 차감: " + ocrResult.getOcrItemName());
        
        log.info("재료 재고 차감: {} ({} -> {})", inventory.getName(), beforeQuantity, newStock);
    }

    /**
     * 재고 변화 로그 생성
     */
    private void createInventoryLog(InventoryEntity inventory, OcrResultEntity ocrResult, 
                                  InventoryLogType logType, BigDecimal quantityChange,
                                  BigDecimal beforeQuantity, BigDecimal afterQuantity, String reason) {
        
        InventoryLogEntity logEntity = InventoryLogEntity.builder()
                .inventory(inventory)
                .ocrResult(ocrResult)
                .logType(logType)
                .quantityChange(quantityChange)
                .beforeQuantity(beforeQuantity)
                .afterQuantity(afterQuantity)
                .reason(reason)
                .build();
                
        inventoryLogRepository.save(logEntity);
    }

    /**
     * 재고 입고 처리 (공급업체 인보이스인 경우)
     */
    @Transactional
    public void restockInventoryFromOcr(OcrResultEntity ocrResult) {
        try {
            if (isInventoryRestock(ocrResult)) {
                restockInventory(ocrResult);
            }
        } catch (Exception e) {
            log.error("재고 입고 처리 중 오류 발생: OCR ID {}, 오류: {}", ocrResult.getOcrId(), e.getMessage(), e);
            throw new RuntimeException("재고 입고 처리에 실패했습니다.", e);
        }
    }

    /**
     * 재고 입고인지 확인 (공급업체 인보이스)
     */
    private boolean isInventoryRestock(OcrResultEntity ocrResult) {
        return ocrResult.getMatchType() != null && 
               "Inventory".equals(ocrResult.getMatchType().getValue());
    }

    /**
     * 재고 입고 처리
     */
    private void restockInventory(OcrResultEntity ocrResult) {
        Integer inventoryId = ocrResult.getTargetId();
        Integer quantity = ocrResult.getQuantity();
        
        InventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다: " + inventoryId));
        
        BigDecimal beforeQuantity = inventory.getCurrentStock();
        BigDecimal restockAmount = BigDecimal.valueOf(quantity);
        BigDecimal afterQuantity = beforeQuantity.add(restockAmount);
        
        inventory.setCurrentStock(afterQuantity);
        inventoryRepository.save(inventory);
        
        // 재고 변화 로그 생성
        createInventoryLog(inventory, ocrResult, InventoryLogType.RESTOCK, 
                          restockAmount, beforeQuantity, afterQuantity,
                          "OCR 매칭을 통한 자동 입고: " + ocrResult.getOcrItemName());
        
        log.info("재고 입고 완료: {} ({} -> {})", inventory.getName(), beforeQuantity, afterQuantity);
    }
}