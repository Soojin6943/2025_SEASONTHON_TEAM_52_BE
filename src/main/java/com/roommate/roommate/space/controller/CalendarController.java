package com.roommate.roommate.space.controller;

import com.roommate.roommate.space.dto.CalendarCreateRequest;
import com.roommate.roommate.space.dto.CalendarResponse;
import com.roommate.roommate.space.dto.CalendarUpdateRequest;
import com.roommate.roommate.space.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Calendar")
@RestController
@RequestMapping("/spaces/{spaceId}/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "일정 생성")
    @PostMapping
    public ResponseEntity<CalendarResponse> createCalendar(
            @PathVariable Long spaceId,
            @RequestBody @Valid CalendarCreateRequest request,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(calendarService.createCalendar(spaceId, request, userId));
    }

    @Operation(summary = "스페이스 일정 목록 조회")
    @GetMapping
    public ResponseEntity<List<CalendarResponse>> getSpaceCalendars(
            @PathVariable Long spaceId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(calendarService.getSpaceCalendars(spaceId, userId));
    }

    @Operation(summary = "특정 일정 조회")
    @GetMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> getCalendar(
            @PathVariable Long spaceId,
            @PathVariable Long calendarId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(calendarService.getCalendar(spaceId, calendarId, userId));
    }

    @Operation(summary = "일정 수정")
    @PutMapping("/{calendarId}")
    public ResponseEntity<CalendarResponse> updateCalendar(
            @PathVariable Long spaceId,
            @PathVariable Long calendarId,
            @RequestBody @Valid CalendarUpdateRequest request,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(calendarService.updateCalendar(spaceId, calendarId, request, userId));
    }

    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{calendarId}")
    public ResponseEntity<Void> deleteCalendar(
            @PathVariable Long spaceId,
            @PathVariable Long calendarId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        calendarService.deleteCalendar(spaceId, calendarId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "월별 캘린더 데이터 조회")
    @GetMapping("/month/{year}/{month}/data")
    public ResponseEntity<Map<String, Object>> getMonthlyCalendarData(
            @PathVariable Long spaceId,
            @PathVariable int year,
            @PathVariable int month,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(calendarService.getMonthlyCalendarData(spaceId, year, month, userId));
    }
}
