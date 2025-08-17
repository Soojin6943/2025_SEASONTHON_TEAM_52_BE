package com.roommate.roommate.space.service;

import com.roommate.roommate.space.dto.CalendarCreateRequest;
import com.roommate.roommate.space.dto.CalendarResponse;
import com.roommate.roommate.space.dto.CalendarUpdateRequest;
import com.roommate.roommate.space.entity.SharedCalendar;
import com.roommate.roommate.space.entity.Space;
import com.roommate.roommate.space.repository.SharedCalendarRepository;
import com.roommate.roommate.space.repository.SpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final SharedCalendarRepository calendarRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final com.roommate.roommate.auth.UserRepository userRepository;

    @Transactional
    public CalendarResponse createCalendar(Long spaceId, CalendarCreateRequest request, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        SharedCalendar calendar = SharedCalendar.builder()
                .space(Space.builder().id(spaceId).build())
                .title(request.title())
                .content(request.content())
                .date(request.date())
                .createdBy(userId)
                .build();

        SharedCalendar savedCalendar = calendarRepository.save(calendar);
        
        // 작성자 이름 조회
        String username = userRepository.findById(userId)
                .map(user -> user.getUsername())
                .orElse("알 수 없음");

        return CalendarResponse.from(savedCalendar, username);
    }

    public List<CalendarResponse> getSpaceCalendars(Long spaceId, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        List<SharedCalendar> calendars = calendarRepository.findBySpaceIdOrderByDateDesc(spaceId);
        
        return calendars.stream()
                .map(calendar -> {
                    String username = userRepository.findById(calendar.getCreatedBy())
                            .map(user -> user.getUsername())
                            .orElse("알 수 없음");
                    return CalendarResponse.from(calendar, username);
                })
                .collect(Collectors.toList());
    }

    public CalendarResponse getCalendar(Long spaceId, Long calendarId, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        // 해당 스페이스의 일정인지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("해당 스페이스의 일정이 아닙니다");
        }

        String username = userRepository.findById(calendar.getCreatedBy())
                .map(user -> user.getUsername())
                .orElse("알 수 없음");

        return CalendarResponse.from(calendar, username);
    }

    @Transactional
    public CalendarResponse updateCalendar(Long spaceId, Long calendarId, CalendarUpdateRequest request, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        // 해당 스페이스의 일정인지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("해당 스페이스의 일정이 아닙니다");
        }

        // 작성자만 수정 가능
        if (!calendar.getCreatedBy().equals(userId)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다");
        }

        calendar.setTitle(request.title());
        calendar.setContent(request.content());
        calendar.setDate(request.date());

        SharedCalendar updatedCalendar = calendarRepository.save(calendar);
        
        String username = userRepository.findById(calendar.getCreatedBy())
                .map(user -> user.getUsername())
                .orElse("알 수 없음");

        return CalendarResponse.from(updatedCalendar, username);
    }

    @Transactional
    public void deleteCalendar(Long spaceId, Long calendarId, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다"));

        // 해당 스페이스의 일정인지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("해당 스페이스의 일정이 아닙니다");
        }

        // 작성자만 삭제 가능
        if (!calendar.getCreatedBy().equals(userId)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다");
        }

        calendarRepository.delete(calendar);
    }

    public Map<String, Object> getMonthlyCalendarData(Long spaceId, int year, int month, Long userId) {
        // 스페이스 멤버인지 확인
        if (!spaceMemberRepository.existsBySpaceIdAndUserId(spaceId, userId)) {
            throw new RuntimeException("스페이스 멤버가 아닙니다");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 해당 월의 모든 날짜에 대한 일정 데이터 생성
        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("year", year);
        calendarData.put("month", month);
        calendarData.put("monthName", yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN));
        
        // 월의 첫 번째 날과 마지막 날
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        
        // 월의 첫 번째 날이 시작하는 요일 (0=일요일, 1=월요일, ...)
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        
        // 월의 총 일수
        int daysInMonth = lastDay.getDayOfMonth();
        
        // 캘린더 그리드 생성
        List<List<Map<String, Object>>> weeks = new ArrayList<>();
        List<Map<String, Object>> currentWeek = new ArrayList<>();
        
        // 첫 번째 주의 빈 날짜들
        for (int i = 0; i < firstDayOfWeek; i++) {
            Map<String, Object> emptyDay = new HashMap<>();
            emptyDay.put("day", null);
            emptyDay.put("events", new ArrayList<>());
            currentWeek.add(emptyDay);
        }
        
        // 월의 모든 날짜
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(year, month, day);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", day);
            dayData.put("date", currentDate.toString());
            dayData.put("isToday", currentDate.equals(LocalDate.now()));
            
            // 해당 날짜의 일정들
            List<SharedCalendar> dayEvents = calendarRepository.findBySpaceIdAndDate(spaceId, currentDate);
            List<Map<String, Object>> events = dayEvents.stream()
                    .map(event -> {
                        String username = userRepository.findById(event.getCreatedBy())
                                .map(user -> user.getUsername())
                                .orElse("알 수 없음");
                        
                        Map<String, Object> eventData = new HashMap<>();
                        eventData.put("id", event.getId());
                        eventData.put("title", event.getTitle());
                        eventData.put("content", event.getContent());
                        eventData.put("createdByUsername", username);
                        return eventData;
                    })
                    .collect(Collectors.toList());
            
            dayData.put("events", events);
            currentWeek.add(dayData);
            
            // 주가 끝나면 다음 주로
            if (currentWeek.size() == 7) {
                weeks.add(new ArrayList<>(currentWeek));
                currentWeek.clear();
            }
        }
        
        // 마지막 주의 빈 날짜들
        while (currentWeek.size() < 7) {
            Map<String, Object> emptyDay = new HashMap<>();
            emptyDay.put("day", null);
            emptyDay.put("events", new ArrayList<>());
            currentWeek.add(emptyDay);
        }
        weeks.add(currentWeek);
        
        calendarData.put("weeks", weeks);
        calendarData.put("firstDayOfWeek", firstDayOfWeek);
        calendarData.put("daysInMonth", daysInMonth);
        
        return calendarData;
    }
}
