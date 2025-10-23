package com.inforsion.inforsionserver.domain.store.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StoreAddressSearchDto {

    @Getter
    @Builder
    @Schema(description = "카카오 주소 검색 응답 DTO")
    public static class Response {
        @Schema(description = "검색된 주소 목록")
        private List<Address> addresses;

        @Schema(description = "검색 결과의 마지막 페이지 여부")
        private Boolean isEnd;

        @Schema(description = "검색 가능한 총 페이지 수")
        private Integer pageableCount;

        @Schema(description = "검색된 전체 건수")
        private Integer totalCount;
    }

    @Getter
    @Builder
    @Schema(description = "카카오 주소 검색 주소 항목")
    public static class Address {
        @Schema(description = "주소명 (지번 혹은 도로명 포함 전체 주소)")
        private String addressName;

        @Schema(description = "도로명 주소")
        private String roadAddressName;

        @Schema(description = "지번 주소")
        private String jibunAddressName;

        @Schema(description = "건물명")
        private String buildingName;

        @Schema(description = "우편번호")
        private String zoneNo;

        @Schema(description = "위도 (Y)")
        private Double latitude;

        @Schema(description = "경도 (X)")
        private Double longitude;
    }
}
