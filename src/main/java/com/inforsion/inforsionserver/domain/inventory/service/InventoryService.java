package com.inforsion.inforsionserver.domain.inventory.service;

import com.inforsion.inforsionserver.domain.inventory.dto.InventoryDto;
import com.inforsion.inforsionserver.domain.inventory.entity.InventoryEntity;
import com.inforsion.inforsionserver.domain.inventory.repository.InventoryRepository;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;

    // 생성
    @Transactional
    public InventoryDto createInventory(@Valid InventoryDto inventoryDto) {
        StoreEntity store = storeRepository.findById(inventoryDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        InventoryEntity entity = InventoryEntity.builder()
                .name(inventoryDto.getName())
                .currentStock(inventoryDto.getCurrentStock())
                .unit(inventoryDto.getUnit())
                .unitCost(inventoryDto.getUnitCost())
                .expiryDate(inventoryDto.getExpiryDate())
                .lastRestockedDate(inventoryDto.getLastRestockedDate())
                .store(store)
                .build();

        InventoryEntity saved = inventoryRepository.save(entity);

        return InventoryDto.fromEntity(saved);

    }

    // 조회
    @Transactional(readOnly = true)
    public Page<InventoryDto> getInventories(Integer storeId, Pageable pageable){
        Page<InventoryEntity> entityPage = inventoryRepository.findInventories(storeId, pageable);
        return entityPage.map(InventoryDto::fromEntity);
    }

    // 수정
    @Transactional
    public InventoryDto updateInventory(Integer inventoryId, @Valid InventoryDto inventoryDto) {
        InventoryEntity entity = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("재고 내역을 찾을 수 없습니다."));

        StoreEntity store = storeRepository.findById(inventoryDto.getStoreId())
                        .orElseThrow(()->new IllegalArgumentException("매장을 찾을 수 없습니다."));

        entity.setName(inventoryDto.getName());
        entity.setCurrentStock(inventoryDto.getCurrentStock());
        entity.setUnitCost(inventoryDto.getUnitCost());
        entity.setExpiryDate(inventoryDto.getExpiryDate());
        entity.setLastRestockedDate(inventoryDto.getLastRestockedDate());
        entity.setStore(store);

        return InventoryDto.fromEntity(entity);
    }

    // 삭제
    @Transactional
    public void deleteInventory(Integer inventoryId) {
        InventoryEntity entity = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Id" + inventoryId + "에 해당하는 재고가 없습니다."));

        inventoryRepository.delete(entity);
    }

    // 유통기한 임박 알림
    @Transactional
    public List<InventoryDto> getExpiringItems(int days){
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return InventoryRepository.findItemsExpiringBefore(targetDate);
    }
}
