package com.roommate.roommate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "로그인 요청(없으면 자동 회원가입)")
public record LoginRequest(
        @Schema(example = "두둥탁")
        @NotBlank(message = "username은 필수입니다.")
        @Size(min = 2, max = 20, message = "2~20자여야 합니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$", message = "한글/영문/숫자/밑줄만 허용됩니다.")
        String username
) {}
