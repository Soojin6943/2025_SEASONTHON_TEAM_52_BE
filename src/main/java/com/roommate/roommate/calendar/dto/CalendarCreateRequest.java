package com.roommate.roommate.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "일정 생성 요청")
public class CalendarCreateRequest {
    
    @Schema(description = "일정 제목", example = "팀 미팅")
    private String title;
    
    @Schema(description = "일정 내용", example = "프로젝트 진행상황 논의")
    private String content;
    
    @Schema(description = "일정 날짜", example = "2025-01-15")
    private LocalDate date;
}
