package com.roommate.roommate.rule.repository;

import com.roommate.roommate.rule.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    
    // 특정 스페이스의 모든 규칙 조회
    List<Rule> findBySpaceId(Long spaceId);
    
    // 특정 스페이스의 특정 요일 규칙 조회
    @Query("SELECT r FROM Rule r WHERE r.space.id = :spaceId AND :dayOfWeek MEMBER OF r.weekdays")
    List<Rule> findBySpaceIdAndWeekday(@Param("spaceId") Long spaceId, @Param("dayOfWeek") String dayOfWeek);
    
    // 특정 기간에 실행되는 규칙 조회
    @Query("SELECT r FROM Rule r WHERE r.space.id = :spaceId " +
           "AND (r.endDate IS NULL OR r.endDate >= :startDate) " +
           "AND r.startDate <= :endDate")
    List<Rule> findRulesInPeriod(@Param("spaceId") Long spaceId, 
                                 @Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
}
