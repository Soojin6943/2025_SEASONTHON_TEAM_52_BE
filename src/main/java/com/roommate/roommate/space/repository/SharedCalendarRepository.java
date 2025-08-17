package com.roommate.roommate.space.repository;

import com.roommate.roommate.space.entity.SharedCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SharedCalendarRepository extends JpaRepository<SharedCalendar, Long> {
    List<SharedCalendar> findBySpaceIdAndDateBetweenOrderByDateAsc(Long spaceId, LocalDate startDate, LocalDate endDate);
    List<SharedCalendar> findBySpaceIdOrderByDateDesc(Long spaceId);
    List<SharedCalendar> findBySpaceIdAndDate(Long spaceId, LocalDate date);
}
