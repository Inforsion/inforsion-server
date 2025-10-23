package com.inforsion.inforsionserver.domain.store.service;

import com.inforsion.inforsionserver.domain.store.dto.external.StoreAddressSearchDto;
import com.inforsion.inforsionserver.domain.store.dto.request.StoreCreateRequest;
import com.inforsion.inforsionserver.domain.store.dto.response.StoreResponse;
import com.inforsion.inforsionserver.domain.store.dto.request.StoreUpdateRequest;
import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import com.inforsion.inforsionserver.domain.store.repository.StoreRepository;
import com.inforsion.inforsionserver.domain.user.entity.UserEntity;
import com.inforsion.inforsionserver.domain.user.repository.UserRepository;
import com.inforsion.inforsionserver.global.error.exception.StoreAccessDeniedException;
import com.inforsion.inforsionserver.global.error.exception.StoreAlreadyExistsException;
import com.inforsion.inforsionserver.global.error.exception.StoreNotFoundException;
import com.inforsion.inforsionserver.global.error.exception.UserNotFoundException;
import com.inforsion.inforsionserver.global.infra.kakao.KakaoMapClient;
import com.inforsion.inforsionserver.global.infra.kakao.dto.KakaoAddressSearchResponse;
import com.inforsion.inforsionserver.global.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
    private final KakaoMapClient kakaoMapClient;

    private static final String S3_DIRECTORY = "stores";

    @Transactional
    public StoreResponse createStore(Integer userId, StoreCreateRequest request) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        String name = sanitizeRequired(request.getName());
        String location = sanitizeRequired(request.getLocation());
        String description = trimToNull(request.getDescription());

        if (storeRepository.existsByNameAndUserId(name, userId)) {
            throw new StoreAlreadyExistsException();
        }

        StoreEntity store = StoreEntity.builder()
                .name(name)
                .location(location)
                .description(description)
                // TODO: 추후 필요시 주석 해제
                // .phoneNumber(request.getPhoneNumber())
                // .email(request.getEmail())
                // .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                // .openingHours(request.getOpeningHours())
                .user(user)
                .build();

        StoreEntity savedStore = storeRepository.save(store);
        return StoreResponse.from(savedStore);
    }

    /**
     * 카카오 맵 주소 검색 API를 이용해 주소 목록을 조회합니다.
     *
     * @param query 검색어
     * @param page  페이지 번호 (1 기준)
     * @param size  페이지당 결과 수
     * @return 가공된 주소 검색 응답 DTO
     */
    public StoreAddressSearchDto.Response searchAddresses(String query, Integer page, Integer size) {
        KakaoAddressSearchResponse kakaoResponse = kakaoMapClient.searchAddress(query, page, size);

        List<StoreAddressSearchDto.Address> addresses = kakaoResponse != null && kakaoResponse.getDocuments() != null
                ? kakaoResponse.getDocuments().stream()
                .map(document -> StoreAddressSearchDto.Address.builder()
                        .addressName(document.getAddressName())
                        .roadAddressName(document.getRoadAddressName())
                        .jibunAddressName(document.getAddress() != null ? document.getAddress().getAddressName() : null)
                        .buildingName(document.getRoadAddress() != null ? document.getRoadAddress().getBuildingName() : null)
                        .zoneNo(document.getRoadAddress() != null ? document.getRoadAddress().getZoneNo() : null)
                        .latitude(parseToDouble(document.getLatitude()))
                        .longitude(parseToDouble(document.getLongitude()))
                        .build())
                .collect(Collectors.toList())
                : List.of();

        KakaoAddressSearchResponse.Meta meta = kakaoResponse != null ? kakaoResponse.getMeta() : null;

        return StoreAddressSearchDto.Response.builder()
                .addresses(addresses)
                .isEnd(meta != null && meta.isEnd())
                .pageableCount(meta != null ? meta.getPageableCount() : addresses.size())
                .totalCount(meta != null ? meta.getTotalCount() : addresses.size())
                .build();
    }

    public StoreResponse getStore(Integer storeId, Integer userId) {
        StoreEntity store = getStoreOwnedBy(storeId, userId);
        return StoreResponse.from(store);
    }

    /**
     * 특정 사용자가 소유한 모든 가게를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자가 소유한 가게 목록
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     */
    public List<StoreResponse> getStoresByUserId(Integer userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<StoreEntity> stores = storeRepository.findByUserId(userId);
        return stores.stream()
                .map(StoreResponse::from)
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
    public List<StoreResponse> getStoresByUserIdAndStatus(Integer userId, Boolean isActive) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<StoreEntity> stores = storeRepository.findByUserIdAndIsActive(userId, isActive);
        return stores.stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreResponse updateStore(Integer storeId, Integer userId, StoreUpdateRequest request) {
        StoreEntity store = getStoreOwnedBy(storeId, userId);

        String sanitizedName = trimToNull(request.getName());
        String sanitizedLocation = trimToNull(request.getLocation());
        String sanitizedDescription = request.getDescription() != null ? request.getDescription().trim() : null;

        if (sanitizedName != null &&
                storeRepository.existsByNameAndUserIdAndIdNot(sanitizedName, store.getUser().getId(), store.getId())) {
            throw new StoreAlreadyExistsException();
        }

        store.update(
                sanitizedName,
                sanitizedLocation,
                sanitizedDescription,
                // TODO: 추후 필요시 주석 해제
                // request.getPhoneNumber(),
                // request.getEmail(),
                // request.getBusinessRegistrationNumber(),
                // request.getOpeningHours(),
                request.getIsActive()
        );

        return StoreResponse.from(store);
    }

    @Transactional
    public void deleteStore(Integer storeId, Integer userId) {
        StoreEntity store = getStoreOwnedBy(storeId, userId);

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
     * @param file 업로드할 이미지 파일
     * @return 업데이트된 가게 정보 DTO
     * @throws StoreNotFoundException 가게가 존재하지 않는 경우
     * @throws IllegalArgumentException 파일이 유효하지 않은 경우
     * @throws RuntimeException S3 업로드 실패 시
     */
    @Transactional
    public StoreResponse uploadStoreThumbnail(Integer storeId, Integer userId, MultipartFile file) {
        StoreEntity store = getStoreOwnedBy(storeId, userId);

        // 기존 이미지가 있다면 S3에서 삭제
        if (store.hasThumbnail()) {
            try {
                s3FileUploadService.deleteFile(store.getThumbnailUrl());
            } catch (Exception e) {
                // 기존 이미지 삭제 실패는 로그만 남기고 진행
            }
        }

        // S3에 새 이미지 업로드
        String imageUrl = s3FileUploadService.uploadImageFile(file, S3_DIRECTORY);
        String s3Key = s3FileUploadService.getS3KeyFromUrl(imageUrl);
        
        // 가게 엔티티에 이미지 정보 저장
        store.updateThumbnailMetadata(imageUrl, file.getOriginalFilename(), s3Key);

        return StoreResponse.from(store);
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
    public void deleteStoreThumbnail(Integer storeId, Integer userId) {
        StoreEntity store = getStoreOwnedBy(storeId, userId);

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

    private String sanitizeRequired(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("필수 값은 공백일 수 없습니다.");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private StoreEntity getStoreOwnedBy(Integer storeId, Integer userId) {
        StoreEntity store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        if (!store.getUser().getId().equals(userId)) {
            throw new StoreAccessDeniedException();
        }
        return store;
    }

    private Double parseToDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
