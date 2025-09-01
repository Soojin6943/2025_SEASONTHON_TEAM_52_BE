package com.roommate.roommate.auth.dto;

import com.roommate.roommate.auth.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record AuthResponse(
        @Schema(description = "유저 ID", example = "1")
        Long userId,
        @Schema(description = "로그인 아이디", example = "두둥탁")
        String username,
        @Schema(description = "유저 나이", example = "25")
        int age,
        @Schema(description = "유저 성별", example = "MALE")
        Gender gender,
        @Schema(description = "이번 호출에서 새로 생성되었는지", example = "true")
        boolean created,
        @Schema(description = "세션 ID", example = "ABC123DEF456")
        String sessionId
) {}
