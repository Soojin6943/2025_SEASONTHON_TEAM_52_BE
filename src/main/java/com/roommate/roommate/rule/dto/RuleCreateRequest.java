package com.roommate.roommate.rule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "규칙 생성 요청")
public class RuleCreateRequest {
    
    @Schema(description = "규칙 내용", example = "격주 수요일마다 집 청소")
    private String content;
    
    @Schema(description = "선택된 요일들", example = "[\"WEDNESDAY\"]")
    private Set<DayOfWeek> weekdays;
    
    @Schema(description = "주기 (1, 2, 3, 4주)", example = "2")
    private Integer weekInterval;
    
    @Schema(description = "시작 날짜", example = "2025-01-15")
    private LocalDate startDate;
    
    @Schema(description = "종료 날짜 (null이면 무제한)", example = "null")
    private LocalDate endDate;
    
    // 기본 검증 메서드
    @Schema(hidden = true)
    public boolean isValid() {
        return content != null && !content.trim().isEmpty() &&
               weekdays != null && !weekdays.isEmpty() &&
               weekInterval != null && weekInterval >= 1 && weekInterval <= 4 &&
               startDate != null;
    }
    
    // 종료일이 시작일보다 이전인지 검증
    @Schema(hidden = true)
    public boolean isEndDateValid() {
        if (endDate == null) return true; // 종료일이 없으면 무제한
        return !endDate.isBefore(startDate);
    }
}
