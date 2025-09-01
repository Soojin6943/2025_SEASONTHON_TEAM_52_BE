package com.roommate.roommate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "프로필 업데이트 요청")
public record UpdateProfileRequest(
        @Schema(description = "자기소개", example = "안녕하세요! 깔끔하고 조용한 룸메이트를 찾고 있습니다.")
        @Size(max = 1000, message = "자기소개는 1000자 이하여야 합니다.")
        String introduction,
        
        @Schema(description = "선호지역 emd_cd", example = "1168010100")
        @Size(max = 10, message = "emd_cd는 10자 이하여야 합니다.")
        String preferredLocationEmdCd,
        
        @Schema(description = "카카오 오픈채팅 링크", example = "https://open.kakao.com/...")
        @Size(max = 500, message = "링크는 500자 이하여야 합니다.")
        String kakaoOpenChatLink
) {}
