package com.roommate.roommate.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 캘린더 응답")
public class MonthlyCalendarResponse {
    
    @Schema(description = "년도")
    private int year;
    
    @Schema(description = "월")
    private int month;
    
    @Schema(description = "주차별 날짜 데이터")
    private List<List<DayData>> weeks;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "일자 데이터")
    public static class DayData {
        
        @Schema(description = "일자")
        private Integer day;
        
        @Schema(description = "오늘 여부")
        private boolean isToday;
        
        @Schema(description = "해당 날짜의 일정 목록")
        private List<CalendarEvent> events;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "캘린더 일정")
    public static class CalendarEvent {
        
        @Schema(description = "일정 ID")
        private Long id;
        
        @Schema(description = "일정 제목")
        private String title;
        
        @Schema(description = "일정 내용")
        private String content;
        
        @Schema(description = "일정 날짜")
        private LocalDate date;
        
        @Schema(description = "생성자 ID")
        private Long createdBy;
    }
}
