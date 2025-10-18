package com.inforsion.inforsionserver.domain.store.dto.response;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "가게 정보 응답 DTO")
public class StoreResponse {

    @Schema(description = "가게 ID")
    private Integer id;

    @Schema(description = "가게 이름")
    private String name;

    @Schema(description = "가게 주소")
    private String location;

    @Schema(description = "가게 설명")
    private String description;

    // TODO: 추후 필요시 주석 해제
    // @Schema(description = "전화번호")
    // private String phoneNumber;
    // @Schema(description = "가게 이메일")
    // private String email;
    // @Schema(description = "사업자 등록번호")
    // private String businessRegistrationNumber;
    // @Schema(description = "영업 시간 정보 (JSON 형식의 문자열)")
    // private String openingHours;

    @Schema(description = "가게 활성화 여부")
    private Boolean isActive;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "가게 소유주 ID")
    private Integer userId;

    @Schema(description = "가게 썸네일 이미지 URL")
    private String thumbnailUrl;

    @Schema(description = "원본 파일명")
    private String originalFileName;

    @Schema(description = "S3 키 (내부 관리용)")
    private String s3Key;

    @Schema(description = "썸네일 이미지 보유 여부")
    private Boolean hasThumbnail;

    public static StoreResponse from(StoreEntity store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .location(store.getLocation())
                .description(store.getDescription())
                // TODO: 추후 필요시 주석 해제
                // .phoneNumber(store.getPhoneNumber())
                // .email(store.getEmail())
                // .businessRegistrationNumber(store.getBusinessRegistrationNumber())
                // .openingHours(store.getOpeningHours())
                .isActive(store.getIsActive())
                .createdAt(store.getCreatedAt())
                .updatedAt(store.getUpdatedAt())
                .userId(store.getUser().getId())
                .thumbnailUrl(store.getThumbnailUrl())
                .originalFileName(store.getOriginalFileName())
                .s3Key(store.getS3Key())
                .hasThumbnail(store.hasThumbnail())
                .build();
    }
}
