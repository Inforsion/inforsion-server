package com.inforsion.inforsionserver.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 비밀번호 검증 응답")
public class StorePasswordVerifyResponse {

    @Schema(description = "매장 ID", example = "1")
    private Integer storeId;

    @Schema(description = "비밀번호 일치 여부", example = "true")
    private Boolean isValid;

    @Schema(description = "메시지", example = "비밀번호가 일치합니다")
    private String message;

    public static StorePasswordVerifyResponse of(Integer storeId, Boolean isValid, String message) {
        return StorePasswordVerifyResponse.builder()
                .storeId(storeId)
                .isValid(isValid)
                .message(message)
                .build();
    }
}