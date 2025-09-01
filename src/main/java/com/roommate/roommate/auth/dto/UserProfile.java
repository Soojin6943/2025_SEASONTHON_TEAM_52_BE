package com.roommate.roommate.auth.dto;

import com.roommate.roommate.auth.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 정보")
public record UserProfile(
        @Schema(description = "유저 ID", example = "1")
        Long userId,
        @Schema(description = "로그인 아이디", example = "두둥탁")
        String username,
        @Schema(description = "유저 나이", example = "25")
        int age,
        @Schema(description = "유저 성별", example = "MALE")
        Gender gender,
        @Schema(description = "자기소개", example = "안녕하세요! 깔끔하고 조용한 룸메이트를 찾고 있습니다.")
        String introduction,
        @Schema(description = "선호지역 emd_cd", example = "1168010100")
        String preferredLocationEmdCd,
        @Schema(description = "현재 스페이스 소속 여부", example = "false")
        boolean hasSpace,
        @Schema(description = "카카오 오픈채팅 링크", example = "https://open.kakao.com/...")
        String kakaoOpenChatLink
) {}
