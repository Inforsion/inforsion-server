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
@Schema(description = "매장 비밀번호 검증 요청")
public class StorePasswordVerifyRequest {

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^\\d{5}$", message = "비밀번호는 5자리 숫자여야 합니다")
    @Schema(description = "확인할 비밀번호 (5자리 숫자)", example = "12345", required = true)
    private String password;
}