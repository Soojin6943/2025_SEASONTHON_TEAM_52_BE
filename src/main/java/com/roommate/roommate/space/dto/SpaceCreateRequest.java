package com.roommate.roommate.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "스페이스 생성 요청")
public record SpaceCreateRequest(
        @Schema(description = "스페이스 이름", example = "자취방")
        @NotBlank(message = "스페이스 이름은 필수입니다.")
        @Size(min = 1, max = 100, message = "1~100자여야 합니다.")
        String name
) {}
