package com.inforsion.inforsionserver.domain.store.dto;

import com.inforsion.inforsionserver.domain.store.entity.StoreEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class StoreDto {

    @Schema(description = "가게 생성 요청 DTO")
    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        @Schema(description = "가게 이름", example = "인퓨전")
        private String name;
        @Schema(description = "가게 주소", example = "서울시 강남구 테헤란로 123")
        private String location;
        @Schema(description = "가게 설명", example = "맛있는 음식을 파는 가게입니다.")
        private String description;
        // TODO: 추후 필요시 주석 해제
        // @Schema(description = "전화번호", example = "010-1234-5678")
        // private String phoneNumber;
        // @Schema(description = "가게 이메일", example = "store@example.com")
        // private String email;
        // @Schema(description = "사업자 등록번호", example = "123-45-67890")
        // private String businessRegistrationNumber;
        // @Schema(description = "영업 시간 정보 (JSON 형식의 문자열)", example = "{\"mon\": \"09:00-18:00\"}")
        // private String openingHours;
    }

    @Schema(description = "가게 정보 수정 요청 DTO")
    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        @Schema(description = "가게 이름", example = "인퓨전 리뉴얼")
        private String name;
        @Schema(description = "가게 주소", example = "서울시 강남구 역삼동 456")
        private String location;
        @Schema(description = "가게 설명", example = "새롭게 단장한 가게입니다.")
        private String description;
        // TODO: 추후 필요시 주석 해제
        // @Schema(description = "전화번호", example = "010-8765-4321")
        // private String phoneNumber;
        // @Schema(description = "가게 이메일", example = "new.store@example.com")
        // private String email;
        // @Schema(description = "사업자 등록번호", example = "111-22-33333")
        // private String businessRegistrationNumber;
        // @Schema(description = "영업 시간 정보 (JSON 형식의 문자열)", example = "{\"tue\": \"10:00-20:00\"}")
        // private String openingHours;
        @Schema(description = "가게 활성화 여부", example = "true")
        private Boolean isActive;
    }

    @Schema(description = "가게 정보 응답 DTO")
    @Getter
    @Builder
    public static class Response {
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

        public static Response from(StoreEntity store) {
            return Response.builder()
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
}
