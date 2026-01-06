package com.inforsion.inforsionserver.domain.store.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d{5}$", message = "현재 비밀번호는 5자리 숫자여야 합니다")
    @Schema(description = "현재 비밀번호 (5자리 숫자)", example = "12345", required = true)
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Pattern(regexp = "^\\d{5}$", message = "새 비밀번호는 5자리 숫자여야 합니다")
    @Schema(description = "새 비밀번호 (5자리 숫자)", example = "54321", required = true)
    private String newPassword;
}