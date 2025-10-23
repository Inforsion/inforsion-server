package com.inforsion.inforsionserver.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "가게 정보 수정 요청 DTO")
public class StoreUpdateRequest {

    @Schema(description = "가게 이름", example = "인포전 리뉴얼")
    @Size(max = 100, message = "가게 이름은 100자를 넘을 수 없습니다.")
    private String name;

    @Schema(description = "가게 주소", example = "서울시 강남구 역삼동 456")
    @Size(max = 255, message = "가게 주소는 255자를 넘을 수 없습니다.")
    private String location;

    @Schema(description = "가게 설명", example = "새롭게 단장한 가게입니다.")
    @Size(max = 1000, message = "가게 설명은 1000자를 넘을 수 없습니다.")
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
