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
    
    // 특정 지역과 주택유형의 최신 월별 통계 조회
    Optional<MonthlyStats> findFirstByRegionCodeAndTypeOrderByCollectMonthDesc(String regionCode, String type);
    
    // 특정 지역의 특정 월 통계 조회
    Optional<MonthlyStats> findByRegionCodeAndCollectMonth(String regionCode, String collectMonth);
    
    // 특정 지역, 주택유형, 월의 통계 조회
    Optional<MonthlyStats> findByRegionCodeAndTypeAndCollectMonth(String regionCode, String type, String collectMonth);
    
    // 특정 지역과 주택유형의 최근 6개월 통계 조회 (트렌드용)
    @Query("SELECT ms FROM MonthlyStats ms WHERE ms.regionCode = :regionCode AND ms.type = :type ORDER BY ms.collectMonth DESC LIMIT 6")
    List<MonthlyStats> findRecent6MonthsByRegionCodeAndType(@Param("regionCode") String regionCode, @Param("type") String type);
    
    // 지역명으로 검색
    List<MonthlyStats> findByRegionNameContaining(String regionName);
    
    // 지역 코드로 검색
    List<MonthlyStats> findByRegionCode(String regionCode);
    
    // 주택유형으로 검색
    List<MonthlyStats> findByType(String type);
    
    // 지역 코드와 주택유형으로 검색
    List<MonthlyStats> findByRegionCodeAndType(String regionCode, String type);
}
