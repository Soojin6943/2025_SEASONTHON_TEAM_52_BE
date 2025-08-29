package com.roommate.roommate.calendar.controller;

import com.roommate.roommate.calendar.dto.CalendarCreateRequest;
import com.roommate.roommate.calendar.dto.CalendarResponse;
import com.roommate.roommate.calendar.dto.CalendarUpdateRequest;
import com.roommate.roommate.calendar.dto.MonthlyCalendarResponse;
import com.roommate.roommate.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/calendars")
@RequiredArgsConstructor
@Tag(name = "공유캘린더", description = "공유캘린더 관련 API")
public class CalendarController {
    
    private final CalendarService calendarService;
    
    @PostMapping
    @Operation(summary = "일정 생성")
    public ResponseEntity<CalendarResponse> createCalendar(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "일정 생성 요청") @RequestBody CalendarCreateRequest request,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        CalendarResponse response = calendarService.createCalendar(spaceId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "일정 목록 조회")
    public ResponseEntity<List<CalendarResponse>> getCalendars(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "시작 날짜") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료 날짜") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        List<CalendarResponse> calendars;
        if (startDate != null && endDate != null) {
            calendars = calendarService.getCalendarsByDateRange(spaceId, startDate, endDate);
        } else {
            calendars = calendarService.getCalendarsBySpace(spaceId);
        }
        
        return ResponseEntity.ok(calendars);
    }
    
    @GetMapping("/{calendarId}")
    @Operation(summary = "일정 상세 조회")
    public ResponseEntity<CalendarResponse> getCalendar(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "일정 ID") @PathVariable Long calendarId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        
        CalendarResponse response = calendarService.getCalendarById(spaceId, calendarId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/month/{year}/{month}")
    @Operation(summary = "월별 캘린더 조회")
    public ResponseEntity<MonthlyCalendarResponse> getMonthlyCalendar(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "년도") @PathVariable int year,
            @Parameter(description = "월") @PathVariable int month) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        MonthlyCalendarResponse response = calendarService.getMonthlyCalendar(spaceId, year, month);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/date/{date}")
    @Operation(summary = "일별 일정 조회")
    public ResponseEntity<List<CalendarResponse>> getCalendarsByDate(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD 형식)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (date == null) {
            throw new RuntimeException("날짜를 입력해주세요.");
        }
        
        List<CalendarResponse> calendars = calendarService.getCalendarsByDate(spaceId, date);
        return ResponseEntity.ok(calendars);
    }
    
    @PutMapping("/{calendarId}")
    @Operation(summary = "일정 수정")
    public ResponseEntity<CalendarResponse> updateCalendar(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "일정 ID") @PathVariable Long calendarId,
            @Parameter(description = "일정 수정 요청") @RequestBody CalendarUpdateRequest request) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        CalendarResponse response = calendarService.updateCalendar(spaceId, calendarId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{calendarId}")
    @Operation(summary = "일정 삭제")
    public ResponseEntity<Void> deleteCalendar(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "캘린더 ID") @PathVariable Long calendarId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        
        calendarService.deleteCalendar(spaceId, calendarId);
        return ResponseEntity.noContent().build();
    }
}
