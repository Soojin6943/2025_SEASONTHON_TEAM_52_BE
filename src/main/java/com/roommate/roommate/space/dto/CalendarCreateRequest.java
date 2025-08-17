package com.roommate.roommate.space.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "공유 캘린더 생성 요청")
public record CalendarCreateRequest(
        @Schema(description = "일정 제목", example = "청소")
        @NotBlank(message = "일정 제목은 필수입니다.")
        @Size(min = 1, max = 100, message = "1~100자여야 합니다.")
        String title,
        
        @Schema(description = "일정 내용", example = "주말 청소")
        @Size(max = 500, message = "500자 이하여야 합니다.")
        String content,
        
        @Schema(description = "일정 날짜", example = "2025-08-12")
        @NotNull(message = "일정 날짜는 필수입니다.")
        LocalDate date
) {}
