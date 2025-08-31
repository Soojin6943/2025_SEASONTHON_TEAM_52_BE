package com.roommate.roommate.rule.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Data
public class RuleResponse {
    private Long id;
    private Long spaceId;
    private String content;
    private Set<DayOfWeek> weekdays;
    private Integer weekInterval;
    private LocalDate startDate;
    private LocalDate endDate;
    
    private Long createdBy; // 작성자 ID
    private String createdByName; // 작성자 이름
    
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
