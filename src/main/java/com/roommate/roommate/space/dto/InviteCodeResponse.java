package com.roommate.roommate.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "초대 코드 응답")
public record InviteCodeResponse(
        @Schema(description = "초대 코드", example = "ABC123DEF")
        String inviteCode,
        
        @Schema(description = "스페이스 이름", example = "자취방")
        String spaceName
) {}
