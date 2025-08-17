package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.StoreNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoreDto.Response createStore(Integer userId, StoreDto.CreateRequest request) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        StoreEntity store = StoreEntity.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .openingHours(request.getOpeningHours())
                .user(user)
                .build();

        StoreEntity savedStore = storeRepository.save(store);
        return StoreDto.Response.from(savedStore);
    }

    public StoreDto.Response getStore(Integer storeId) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        return StoreDto.Response.from(store);
    }

    @Transactional
    public StoreDto.Response updateStore(Integer storeId, StoreDto.UpdateRequest request) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        // TODO: 사용자 권한 확인 로직 추가 필요 (로그인한 사용자가 가게 주인인지)

        store.update(
                request.getName(),
                request.getLocation(),
                request.getDescription(),
                request.getPhoneNumber(),
                request.getEmail(),
                request.getBusinessRegistrationNumber(),
                request.getOpeningHours(),
                request.getIsActive()
        );

        return StoreDto.Response.from(store);
    }

    @Transactional
    public void deleteStore(Integer storeId) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        // TODO: 사용자 권한 확인 로직 추가 필요

        storeRepository.delete(store);
    }
}
