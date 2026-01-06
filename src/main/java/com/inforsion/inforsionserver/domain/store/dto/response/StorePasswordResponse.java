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
@Schema(description = "매장 비밀번호 응답")
public class StorePasswordResponse {

    @Schema(description = "매장 ID", example = "1")
    private Integer storeId;

    @Schema(description = "비밀번호 설정 여부", example = "true")
    private Boolean hasPassword;

    @Schema(description = "메시지", example = "비밀번호가 설정되었습니다")
    private String message;

    public static StorePasswordResponse of(Integer storeId, Boolean hasPassword, String message) {
        return StorePasswordResponse.builder()
                .storeId(storeId)
                .hasPassword(hasPassword)
                .message(message)
                .build();
    }
}