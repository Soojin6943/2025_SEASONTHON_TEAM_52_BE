package com.roommate.roommate.calendar.repository;

import com.roommate.roommate.calendar.entity.SharedCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SharedCalendarRepository extends JpaRepository<SharedCalendar, Long> {
    
    @Query("SELECT c FROM SharedCalendar c WHERE c.space.id = :spaceId ORDER BY c.date ASC")
    List<SharedCalendar> findBySpaceIdOrderByDateAsc(@Param("spaceId") Long spaceId);
    
    @Query("SELECT c FROM SharedCalendar c WHERE c.space.id = :spaceId AND c.date BETWEEN :startDate AND :endDate ORDER BY c.date ASC")
    List<SharedCalendar> findBySpaceIdAndDateBetween(@Param("spaceId") Long spaceId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM SharedCalendar c WHERE c.space.id = :spaceId AND c.date = :date ORDER BY c.createdAt ASC")
    List<SharedCalendar> findBySpaceIdAndDate(@Param("spaceId") Long spaceId, @Param("date") LocalDate date);
}
