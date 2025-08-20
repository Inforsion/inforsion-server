package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.StoreDto;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.StoreNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import com.inforsion.inforsionserver.global.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final S3FileUploadService s3FileUploadService;

    private static final String S3_DIRECTORY = "stores";

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

    /**
     * 특정 사용자가 소유한 모든 가게를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자가 소유한 가게 목록
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    public List<StoreDto.Response> getStoresByUserId(Integer userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<StoreEntity> stores = storeRepository.findByUserId(userId);
        return stores.stream()
                .map(StoreDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 소유한 활성 상태인 가게만 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param isActive 활성 상태 (true: 활성, false: 비활성)
     * @return 조건에 맞는 가게 목록
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    public List<StoreDto.Response> getStoresByUserIdAndStatus(Integer userId, Boolean isActive) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<StoreEntity> stores = storeRepository.findByUserIdAndIsActive(userId, isActive);
        return stores.stream()
                .map(StoreDto.Response::from)
                .collect(Collectors.toList());
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

        // S3에서 썸네일 이미지 삭제
        if (store.hasThumbnail()) {
            try {
                s3FileUploadService.deleteFile(store.getThumbnailUrl());
            } catch (Exception e) {
                // 이미지 삭제 실패는 로그만 남기고 진행
            }
        }

        storeRepository.delete(store);
    }

    /**
     * 가게 썸네일 이미지를 업로드합니다.
     * 
     * 멀티파트 파일을 받아 S3에 업로드하고, 가게 엔티티에 이미지 정보를 저장합니다.
     * 기존 이미지가 있는 경우 새 이미지로 교체됩니다.
     * 
     * @param storeId 가게 ID
     * @param image 업로드할 이미지 파일
     * @return 업데이트된 가게 정보 DTO
     * @throws StoreNotFoundException 가게가 존재하지 않는 경우
     * @throws IllegalArgumentException 파일이 유효하지 않은 경우
     * @throws RuntimeException S3 업로드 실패 시
     */
    @Transactional
    public StoreDto.Response uploadStoreThumbnail(Integer storeId, MultipartFile image) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        // TODO: 사용자 권한 확인 로직 추가 필요

        // 기존 이미지가 있다면 S3에서 삭제
        if (store.hasThumbnail()) {
            try {
                s3FileUploadService.deleteFile(store.getThumbnailUrl());
            } catch (Exception e) {
                // 기존 이미지 삭제 실패는 로그만 남기고 진행
            }
        }

        // S3에 새 이미지 업로드
        String imageUrl = s3FileUploadService.uploadImageFile(image, S3_DIRECTORY);
        String s3Key = s3FileUploadService.getS3KeyFromUrl(imageUrl);
        
        // 가게 엔티티에 이미지 정보 저장
        store.updateThumbnailMetadata(imageUrl, image.getOriginalFilename(), s3Key);

        return StoreDto.Response.from(store);
    }

    /**
     * 가게 썸네일 이미지를 삭제합니다.
     * 
     * S3에서 실제 파일을 삭제하고, 가게 엔티티에서 이미지 관련 정보를 제거합니다.
     * 
     * @param storeId 가게 ID
     * @throws StoreNotFoundException 가게가 존재하지 않는 경우
     */
    @Transactional
    public void deleteStoreThumbnail(Integer storeId) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        // TODO: 사용자 권한 확인 로직 추가 필요

        // S3에서 실제 파일 삭제
        if (store.hasThumbnail()) {
            try {
                s3FileUploadService.deleteFile(store.getThumbnailUrl());
            } catch (Exception e) {
                // S3 삭제 실패 시 로그만 남기고 DB는 업데이트
            }
        }

        // 가게 엔티티에서 이미지 정보 제거
        store.updateThumbnailMetadata(null, null, null);
    }
}
