package com.inforsion.inforsionserver.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 비밀번호 변경 요청")
public class StorePasswordUpdateRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    @Schema(description = "현재 비밀번호", example = "store1234", required = true)
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 4, max = 20, message = "새 비밀번호는 4자 이상 20자 이하여야 합니다")
    @Schema(description = "새 비밀번호", example = "newstore1234", required = true)
    private String newPassword;
}