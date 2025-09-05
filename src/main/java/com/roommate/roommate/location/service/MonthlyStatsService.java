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
    
    // 특정 지역과 주택유형의 최신 월별 통계 조회 (평균 월세만)
    public AvgMonthlyRentResponse getLatestMonthlyStatsByType(String regionCode, String type) {
        MonthlyStats stats = monthlyStatsRepository
                .findFirstByRegionCodeAndTypeOrderByCollectMonthDesc(regionCode, type)
                .orElseThrow(() -> new RuntimeException("해당 지역과 주택유형의 통계 데이터를 찾을 수 없습니다."));
        
        return convertToAvgMonthlyRentResponse(stats);
    }
    
    // 특정 지역, 주택유형, 면적대의 최신 월별 통계 조회 (면적별 데이터만)
    public AreaStatsResponse getLatestAreaMonthlyStatsByType(String regionCode, String type, String areaRange) {
        MonthlyStats stats = monthlyStatsRepository
                .findFirstByRegionCodeAndTypeOrderByCollectMonthDesc(regionCode, type)
                .orElseThrow(() -> new RuntimeException("해당 지역과 주택유형의 통계 데이터를 찾을 수 없습니다."));
        
        // 디버깅을 위한 로그 추가
        System.out.println("=== DB 데이터 확인 ===");
        System.out.println("Region: " + stats.getRegionCode() + ", Type: " + stats.getType());
        System.out.println("Area 2025: count=" + stats.getArea2025Count() + ", median=" + stats.getArea2025Median() + ", avg=" + stats.getArea2025Avg());
        System.out.println("Area 2530: count=" + stats.getArea2530Count() + ", median=" + stats.getArea2530Median() + ", avg=" + stats.getArea2530Avg());
        System.out.println("Area 3035: count=" + stats.getArea3035Count() + ", median=" + stats.getArea3035Median() + ", avg=" + stats.getArea3035Avg());
        System.out.println("Area 3540: count=" + stats.getArea3540Count() + ", median=" + stats.getArea3540Median() + ", avg=" + stats.getArea3540Avg());
        System.out.println("Requested areaRange: " + areaRange);
        
        return convertToAreaStatsResponse(stats, areaRange);
    }
    
    // 특정 지역의 특정 월 통계 조회
    public MonthlyStatsResponse getMonthlyStats(String regionCode, String collectMonth) {
        MonthlyStats stats = monthlyStatsRepository
                .findByRegionCodeAndCollectMonth(regionCode, collectMonth)
                .orElseThrow(() -> new RuntimeException("해당 월의 통계 데이터를 찾을 수 없습니다."));
        
        return convertToResponse(stats);
    }
    
    // 특정 지역, 주택유형, 월의 통계 조회
    public MonthlyStatsResponse getMonthlyStatsByType(String regionCode, String type, String collectMonth) {
        MonthlyStats stats = monthlyStatsRepository
                .findByRegionCodeAndTypeAndCollectMonth(regionCode, type, collectMonth)
                .orElseThrow(() -> new RuntimeException("해당 조건의 통계 데이터를 찾을 수 없습니다."));
        
        return convertToResponse(stats);
    }
    
    // 특정 지역과 주택유형의 최근 6개월 트렌드 조회
    public TrendResponse getRecentTrendByType(String regionCode, String type) {
        List<MonthlyStats> recentStats = monthlyStatsRepository.findRecent6MonthsByRegionCodeAndType(regionCode, type);
        
        if (recentStats.isEmpty()) {
            throw new RuntimeException("해당 지역과 주택유형의 트렌드 데이터를 찾을 수 없습니다.");
        }
        
        String regionName = recentStats.get(0).getRegionName();
        
        List<MonthlyTrendData> trendData = recentStats.stream()
                .map(this::convertToTrendData)
                .collect(Collectors.toList());
        
        return new TrendResponse(regionCode + "_" + type, regionName, trendData);
    }
    
    // 지역명으로 검색
    public List<MonthlyStatsResponse> searchByRegionName(String regionName) {
        List<MonthlyStats> statsList = monthlyStatsRepository.findByRegionNameContaining(regionName);
        
        return statsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 주택유형으로 검색
    public List<MonthlyStatsResponse> searchByType(String type) {
        List<MonthlyStats> statsList = monthlyStatsRepository.findByType(type);
        
        return statsList.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 지역 코드와 주택유형으로 검색
    public List<MonthlyStatsResponse> searchByRegionCodeAndType(String regionCode, String type) {
        List<MonthlyStats> statsList = monthlyStatsRepository.findByRegionCodeAndType(regionCode, type);
        
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
                stats.getType(),
                stats.getCollectMonth(),
                stats.getTotalContracts(),
                stats.getYouthContracts(),
                stats.getYouthContractsClean(),
                stats.getAvgMonthlyRent(),
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
    
    // 면적대별 MonthlyStats를 MonthlyStatsResponse로 변환
    private MonthlyStatsResponse convertToAreaResponse(MonthlyStats stats, String areaRange) {
        AreaInfo areaInfo = getAreaInfoByRange(stats, areaRange);
        
        // 면적대에 따라 올바른 위치에 데이터 배치
        AreaDetailResponse areaDetail = switch (areaRange.toUpperCase()) {
            case "2025" -> new AreaDetailResponse(areaInfo, null, null, null);
            case "2530" -> new AreaDetailResponse(null, areaInfo, null, null);
            case "3035" -> new AreaDetailResponse(null, null, areaInfo, null);
            case "3540" -> new AreaDetailResponse(null, null, null, areaInfo);
            default -> throw new RuntimeException("지원하지 않는 면적대입니다. (2025, 2530, 3035, 3540 중 선택)");
        };
        
        return new MonthlyStatsResponse(
                stats.getRegionCode(),
                stats.getRegionName(),
                stats.getType(),
                stats.getCollectMonth(),
                stats.getTotalContracts(),
                stats.getYouthContracts(),
                stats.getYouthContractsClean(),
                stats.getAvgMonthlyRent(),
                stats.getYouthMedianMonthlyRent(),
                stats.getYouthAvgMonthlyRent(),
                areaDetail
        );
    }
    
    // 면적대에 따른 AreaInfo 반환
    private AreaInfo getAreaInfoByRange(MonthlyStats stats, String areaRange) {
        return switch (areaRange.toUpperCase()) {
            case "2025" -> new AreaInfo(stats.getArea2025Count(), stats.getArea2025Median(), stats.getArea2025Avg());
            case "2530" -> new AreaInfo(stats.getArea2530Count(), stats.getArea2530Median(), stats.getArea2530Avg());
            case "3035" -> new AreaInfo(stats.getArea3035Count(), stats.getArea3035Median(), stats.getArea3035Avg());
            case "3540" -> new AreaInfo(stats.getArea3540Count(), stats.getArea3540Median(), stats.getArea3540Avg());
            default -> throw new RuntimeException("지원하지 않는 면적대입니다. (2025, 2530, 3035, 3540 중 선택)");
        };
    }
    
    // 면적별 통계 응답으로 변환
    private AreaStatsResponse convertToAreaStatsResponse(MonthlyStats stats, String areaRange) {
        AreaInfo areaInfo = getAreaInfoByRange(stats, areaRange);
        
        return new AreaStatsResponse(
                stats.getRegionCode(),
                stats.getRegionName(),
                stats.getType(),
                stats.getCollectMonth(),
                areaRange,
                areaInfo
        );
    }
    
    // 평균 월세 통계 응답으로 변환
    private AvgMonthlyRentResponse convertToAvgMonthlyRentResponse(MonthlyStats stats) {
        return new AvgMonthlyRentResponse(
                stats.getRegionCode(),
                stats.getRegionName(),
                stats.getType(),
                stats.getCollectMonth(),
                stats.getAvgMonthlyRent()
        );
    }
}
