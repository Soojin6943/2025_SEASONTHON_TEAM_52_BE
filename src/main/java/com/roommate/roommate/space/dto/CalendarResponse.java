package com.roommate.roommate.space.dto;

import com.roommate.roommate.space.entity.SharedCalendar;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "공유 캘린더 응답")
public record CalendarResponse(
        @Schema(description = "일정 ID", example = "1")
        Long id,
        
        @Schema(description = "일정 제목", example = "청소")
        String title,
        
        @Schema(description = "일정 내용", example = "주말 청소")
        String content,
        
        @Schema(description = "일정 날짜", example = "2025-08-12")
        LocalDate date,
        
        @Schema(description = "작성자 이름", example = "두둥탁")
        String createdByUsername,
        
        @Schema(description = "작성일", example = "2025-08-12T10:00:00")
        LocalDateTime createdAt
) {
    public static CalendarResponse from(SharedCalendar calendar, String username) {
        return new CalendarResponse(
                calendar.getId(),
                calendar.getTitle(),
                calendar.getContent(),
                calendar.getDate(),
                username,
                calendar.getCreatedAt()
        );
    }
}
