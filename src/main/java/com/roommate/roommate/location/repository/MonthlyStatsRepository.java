package com.roommate.roommate.location.repository;

import com.roommate.roommate.location.entity.MonthlyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyStatsRepository extends JpaRepository<MonthlyStats, Long> {
    
    // 특정 지역의 최신 월별 통계 조회
    Optional<MonthlyStats> findFirstByRegionCodeOrderByCollectMonthDesc(String regionCode);
    
    // 특정 지역의 특정 월 통계 조회
    Optional<MonthlyStats> findByRegionCodeAndCollectMonth(String regionCode, String collectMonth);
    
    // 특정 지역의 최근 6개월 통계 조회 (트렌드용)
    @Query("SELECT ms FROM MonthlyStats ms WHERE ms.regionCode = :regionCode ORDER BY ms.collectMonth DESC LIMIT 6")
    List<MonthlyStats> findRecent6MonthsByRegionCode(@Param("regionCode") String regionCode);
    
    // 지역명으로 검색
    List<MonthlyStats> findByRegionNameContaining(String regionName);
    
    // 지역 코드로 검색
    List<MonthlyStats> findByRegionCode(String regionCode);
}
