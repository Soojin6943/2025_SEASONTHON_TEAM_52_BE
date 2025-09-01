package com.roommate.roommate.location.service;

import com.roommate.roommate.location.dto.*;
import com.roommate.roommate.location.entity.MonthlyStats;
import com.roommate.roommate.location.repository.MonthlyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyStatsService {
    
    private final MonthlyStatsRepository monthlyStatsRepository;
    
    // 특정 지역의 최신 월별 통계 조회
    public MonthlyStatsResponse getLatestMonthlyStats(String regionCode) {
        MonthlyStats stats = monthlyStatsRepository
                .findFirstByRegionCodeOrderByCollectMonthDesc(regionCode)
                .orElseThrow(() -> new RuntimeException("해당 지역의 통계 데이터를 찾을 수 없습니다."));
        
        return convertToResponse(stats);
    }
    
    // 특정 지역의 특정 월 통계 조회
    public MonthlyStatsResponse getMonthlyStats(String regionCode, String collectMonth) {
        MonthlyStats stats = monthlyStatsRepository
                .findByRegionCodeAndCollectMonth(regionCode, collectMonth)
                .orElseThrow(() -> new RuntimeException("해당 월의 통계 데이터를 찾을 수 없습니다."));
        
        return convertToResponse(stats);
    }
    
    // 특정 지역의 최근 6개월 트렌드 조회
    public TrendResponse getRecentTrend(String regionCode) {
        List<MonthlyStats> recentStats = monthlyStatsRepository.findRecent6MonthsByRegionCode(regionCode);
        
        if (recentStats.isEmpty()) {
            throw new RuntimeException("해당 지역의 트렌드 데이터를 찾을 수 없습니다.");
        }
        
        String regionName = recentStats.get(0).getRegionName();
        
        List<MonthlyTrendData> trendData = recentStats.stream()
                .map(this::convertToTrendData)
                .collect(Collectors.toList());
        
        return new TrendResponse(regionCode, regionName, trendData);
    }
    
    // 지역명으로 검색
    public List<MonthlyStatsResponse> searchByRegionName(String regionName) {
        List<MonthlyStats> statsList = monthlyStatsRepository.findByRegionNameContaining(regionName);
        
        return statsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // MonthlyStats를 MonthlyStatsResponse로 변환
    private MonthlyStatsResponse convertToResponse(MonthlyStats stats) {
        AreaDetailResponse areaDetail = new AreaDetailResponse(
                new AreaInfo(stats.getArea2025Count(), stats.getArea2025Median(), stats.getArea2025Avg()),
                new AreaInfo(stats.getArea2530Count(), stats.getArea2530Median(), stats.getArea2530Avg()),
                new AreaInfo(stats.getArea3035Count(), stats.getArea3035Median(), stats.getArea3035Avg()),
                new AreaInfo(stats.getArea3540Count(), stats.getArea3540Median(), stats.getArea3540Avg())
        );
        
        return new MonthlyStatsResponse(
                stats.getRegionCode(),
                stats.getRegionName(),
                stats.getCollectMonth(),
                stats.getTotalContracts(),
                stats.getYouthContracts(),
                stats.getYouthMedianMonthlyRent(),
                stats.getYouthAvgMonthlyRent(),
                areaDetail
        );
    }
    
    // MonthlyStats를 MonthlyTrendData로 변환
    private MonthlyTrendData convertToTrendData(MonthlyStats stats) {
        return new MonthlyTrendData(
                stats.getCollectMonth(),
                stats.getYouthMedianMonthlyRent(),
                stats.getYouthAvgMonthlyRent(),
                stats.getTotalContracts()
        );
    }
}
