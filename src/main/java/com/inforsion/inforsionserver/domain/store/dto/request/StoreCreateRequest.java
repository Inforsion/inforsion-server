package com.inforsion.inforsionserver.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "가게 생성 요청 DTO")
public class StoreCreateRequest {

    @Schema(description = "가게 이름", example = "인포전")
    @NotBlank(message = "가게 이름은 필수 입력값입니다.")
    @Size(max = 100, message = "가게 이름은 100자를 넘을 수 없습니다.")
    private String name;

    @Schema(description = "가게 주소", example = "서울시 강남구 테헤란로 123")
    @NotBlank(message = "가게 주소는 필수 입력값입니다.")
    @Size(max = 255, message = "가게 주소는 255자를 넘을 수 없습니다.")
    private String location;

    @Schema(description = "가게 설명", example = "맛있는 음식을 파는 가게입니다.")
    @Size(max = 1000, message = "가게 설명은 1000자를 넘을 수 없습니다.")
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
