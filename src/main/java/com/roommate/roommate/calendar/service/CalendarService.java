package com.roommate.roommate.calendar.service;

import com.roommate.roommate.calendar.dto.CalendarCreateRequest;
import com.roommate.roommate.calendar.dto.CalendarResponse;
import com.roommate.roommate.calendar.dto.CalendarUpdateRequest;
import com.roommate.roommate.calendar.dto.MonthlyCalendarResponse;
import com.roommate.roommate.calendar.entity.SharedCalendar;
import com.roommate.roommate.calendar.repository.SharedCalendarRepository;
import com.roommate.roommate.space.entity.Space;
import com.roommate.roommate.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    
    private final SharedCalendarRepository calendarRepository;
    private final SpaceRepository spaceRepository;
    
    @Transactional
    public CalendarResponse createCalendar(Long spaceId, CalendarCreateRequest request, Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("스페이스를 찾을 수 없습니다. (ID: " + spaceId + ")"));
        
        SharedCalendar calendar = SharedCalendar.builder()
                .space(space)
                .title(request.getTitle())
                .content(request.getContent())
                .date(request.getDate())
                .createdBy(userId)
                .build();
        
        SharedCalendar saved = calendarRepository.save(calendar);
        return mapToResponse(saved);
    }
    
    public List<CalendarResponse> getCalendarsBySpace(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<SharedCalendar> calendars = calendarRepository.findBySpaceIdOrderByDateAsc(spaceId);
        return calendars.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<CalendarResponse> getCalendarsByDateRange(Long spaceId, LocalDate startDate, LocalDate endDate) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (startDate == null || endDate == null) {
            throw new RuntimeException("시작일과 종료일을 입력해주세요.");
        }
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("시작일은 종료일보다 이전이어야 합니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<SharedCalendar> calendars = calendarRepository.findBySpaceIdAndDateBetween(spaceId, startDate, endDate);
        return calendars.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<CalendarResponse> getCalendarsByDate(Long spaceId, LocalDate date) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (date == null) {
            throw new RuntimeException("날짜를 입력해주세요.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<SharedCalendar> calendars = calendarRepository.findBySpaceIdAndDate(spaceId, date);
        return calendars.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public CalendarResponse getCalendarById(Long spaceId, Long calendarId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("캘린더를 찾을 수 없습니다. (ID: " + calendarId + ")"));
        
        // 캘린더가 해당 스페이스에 속하는지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("캘린더가 해당 스페이스에 속하지 않습니다. (캘린더 ID: " + calendarId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        return mapToResponse(calendar);
    }
    
    public MonthlyCalendarResponse getMonthlyCalendar(Long spaceId, int year, int month) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (year < 1900 || year > 2100) {
            throw new RuntimeException("유효하지 않은 연도입니다. (연도: " + year + ")");
        }
        if (month < 1 || month > 12) {
            throw new RuntimeException("유효하지 않은 월입니다. (월: " + month + ")");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        // 해당 월의 시작일과 종료일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        // 해당 월의 일정 조회
        List<SharedCalendar> calendars = calendarRepository.findBySpaceIdAndDateBetween(spaceId, startDate, endDate);
        
        // 월별 캘린더 데이터 생성
        List<List<MonthlyCalendarResponse.DayData>> weeks = generateMonthlyCalendar(year, month, calendars);
        
        return MonthlyCalendarResponse.builder()
                .year(year)
                .month(month)
                .weeks(weeks)
                .build();
    }
    
    @Transactional
    public CalendarResponse updateCalendar(Long spaceId, Long calendarId, CalendarUpdateRequest request) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("캘린더를 찾을 수 없습니다. (ID: " + calendarId + ")"));
        
        // 캘린더가 해당 스페이스에 속하는지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("캘린더가 해당 스페이스에 속하지 않습니다. (캘린더 ID: " + calendarId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        calendar.setTitle(request.getTitle());
        calendar.setContent(request.getContent());
        calendar.setDate(request.getDate());
        
        return mapToResponse(calendar);
    }
    
    @Transactional
    public void deleteCalendar(Long spaceId, Long calendarId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (calendarId == null || calendarId <= 0) {
            throw new RuntimeException("유효하지 않은 캘린더 ID입니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        SharedCalendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("캘린더를 찾을 수 없습니다. (ID: " + calendarId + ")"));
        
        // 캘린더가 해당 스페이스에 속하는지 확인
        if (!calendar.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("캘린더가 해당 스페이스에 속하지 않습니다. (캘린더 ID: " + calendarId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        calendarRepository.deleteById(calendarId);
    }
    
    private MonthlyCalendarResponse.DayData createDayData(int day, List<SharedCalendar> events, boolean isToday) {
        List<MonthlyCalendarResponse.CalendarEvent> calendarEvents = events.stream()
                .map(this::mapToCalendarEvent)
                .collect(Collectors.toList());
        
        return MonthlyCalendarResponse.DayData.builder()
                .day(day)
                .isToday(isToday)
                .events(calendarEvents)
                .build();
    }
    
    private MonthlyCalendarResponse.CalendarEvent mapToCalendarEvent(SharedCalendar calendar) {
        return MonthlyCalendarResponse.CalendarEvent.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .content(calendar.getContent())
                .date(calendar.getDate())
                .createdBy(calendar.getCreatedBy())
                .build();
    }
    
    private List<List<MonthlyCalendarResponse.DayData>> generateMonthlyCalendar(int year, int month, List<SharedCalendar> calendars) {
        List<List<MonthlyCalendarResponse.DayData>> weeks = new ArrayList<>();
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        
        // 첫 번째 주의 시작일 (일요일)
        LocalDate weekStart = firstDay.minusDays(firstDay.getDayOfWeek().getValue() % 7);
        
        LocalDate today = LocalDate.now();
        
        LocalDate currentDate = weekStart;
        List<MonthlyCalendarResponse.DayData> currentWeek = new ArrayList<>();
        
        while (!currentDate.isAfter(lastDay.plusDays(6))) {
            if (currentDate.getDayOfWeek().getValue() == 1 && !currentWeek.isEmpty()) {
                weeks.add(currentWeek);
                currentWeek = new ArrayList<>();
            }
            
            if (currentDate.isBefore(firstDay) || currentDate.isAfter(lastDay)) {
                // 해당 월이 아닌 날짜
                currentWeek.add(MonthlyCalendarResponse.DayData.builder()
                        .day(null)
                        .isToday(false)
                        .events(new ArrayList<>())
                        .build());
            } else {
                // 해당 월의 날짜
                int dayOfMonth = currentDate.getDayOfMonth();
                boolean isToday = currentDate.equals(today);
                
                // 해당 날짜의 일정 필터링 - currentDate를 final 변수로 복사
                final LocalDate finalCurrentDate = currentDate;
                List<SharedCalendar> dayEvents = calendars.stream()
                        .filter(cal -> cal.getDate().equals(finalCurrentDate))
                        .collect(Collectors.toList());
                
                currentWeek.add(createDayData(dayOfMonth, dayEvents, isToday));
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        // 마지막 주 추가
        if (!currentWeek.isEmpty()) {
            weeks.add(currentWeek);
        }
        
        return weeks;
    }
    
    private CalendarResponse mapToResponse(SharedCalendar calendar) {
        return CalendarResponse.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .content(calendar.getContent())
                .date(calendar.getDate())
                .createdBy(calendar.getCreatedBy())
                .createdAt(calendar.getCreatedAt())
                .build();
    }
}
