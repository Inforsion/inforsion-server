package com.inforsion.inforsionserver.global.infra.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KakaoAddressSearchResponse {

    private List<Document> documents;
    private Meta meta;

    @Getter
    public static class Document {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("road_address_name")
        private String roadAddressName;

        /**
         * 경도(LNG)
         */
        @JsonProperty("x")
        private String longitude;

        /**
         * 위도(LAT)
         */
        @JsonProperty("y")
        private String latitude;

        @JsonProperty("address_type")
        private String addressType;

        private Address address;

        @JsonProperty("road_address")
        private RoadAddress roadAddress;
    }

    @Getter
    public static class Address {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        @JsonProperty("region_2depth_name")
        private String region2DepthName;

        @JsonProperty("region_3depth_name")
        private String region3DepthName;

        @JsonProperty("region_3depth_h_name")
        private String region3DepthHName;

        @JsonProperty("main_address_no")
        private String mainAddressNo;

        @JsonProperty("sub_address_no")
        private String subAddressNo;

        private String mountainYn;

        @JsonProperty("zip_code")
        private String zipCode;

        @JsonProperty("x")
        private String longitude;

        @JsonProperty("y")
        private String latitude;
    }

    @Getter
    public static class RoadAddress {
        @JsonProperty("address_name")
        private String addressName;

        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        @JsonProperty("region_2depth_name")
        private String region2DepthName;

        @JsonProperty("region_3depth_name")
        private String region3DepthName;

        @JsonProperty("road_name")
        private String roadName;

        @JsonProperty("underground_yn")
        private String undergroundYn;

        @JsonProperty("main_building_no")
        private String mainBuildingNo;

        @JsonProperty("sub_building_no")
        private String subBuildingNo;

        @JsonProperty("building_name")
        private String buildingName;

        @JsonProperty("zone_no")
        private String zoneNo;

        @JsonProperty("x")
        private String longitude;

        @JsonProperty("y")
        private String latitude;
    }

    @Getter
    public static class Meta {
        @JsonProperty("is_end")
        private boolean isEnd;

        @JsonProperty("pageable_count")
        private int pageableCount;

        @JsonProperty("total_count")
        private int totalCount;
    }
}
