package com.roommate.roommate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "스페이스 멤버 정보 DTO")
public class SpaceMemberResponse {
    
    @Schema(description = "사용자 ID")
    private Long userId;
    
    @Schema(description = "사용자 이름")
    private String username;
    
    @Schema(description = "스페이스 내 역할")
    private String role;
    
    @Schema(description = "가입 날짜")
    private String joinedAt;
}
