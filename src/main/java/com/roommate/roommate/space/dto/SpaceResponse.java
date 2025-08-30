package com.roommate.roommate.space.dto;

import com.roommate.roommate.space.entity.Space;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "스페이스 응답")
public record SpaceResponse(
        @Schema(description = "스페이스 ID", example = "1")
        Long id,
        
        @Schema(description = "스페이스 이름", example = "우리집")
        String name,
        
        @Schema(description = "최대 멤버 수", example = "4")
        Integer maxMembers,
        
        @Schema(description = "현재 멤버 수", example = "2")
        Integer currentMembers,
        
        @Schema(description = "생성일", example = "2025-08-12T10:00:00")
        LocalDateTime createdAt
) {
    public static SpaceResponse from(Space space) {
        return new SpaceResponse(
                space.getId(),
                space.getName(),
                space.getMaxMembers(),
                null, // currentMembers는 별도로 계산 필요
                space.getCreatedAt()
        );
    }
}
