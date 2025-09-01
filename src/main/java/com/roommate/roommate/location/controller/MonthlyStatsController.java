package com.roommate.roommate.location.controller;

import com.roommate.roommate.location.dto.MonthlyStatsResponse;
import com.roommate.roommate.location.dto.TrendResponse;
import com.roommate.roommate.location.service.MonthlyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Monthly Stats", description = "월별 통계 API")
@RestController
@RequestMapping("/api/monthly-stats")
@RequiredArgsConstructor
public class MonthlyStatsController {
    
    private final MonthlyStatsService monthlyStatsService;
    
    @Operation(summary = "지역별 최신 월세 통계 조회", description = "특정 지역의 최신 월별 통계를 조회합니다.")
    @GetMapping("/{regionCode}/latest")
    public ResponseEntity<MonthlyStatsResponse> getLatestMonthlyStats(
            @PathVariable String regionCode) {
        MonthlyStatsResponse response = monthlyStatsService.getLatestMonthlyStats(regionCode);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역별 특정 월 통계 조회", description = "특정 지역의 특정 월 통계를 조회합니다.")
    @GetMapping("/{regionCode}/month/{collectMonth}")
    public ResponseEntity<MonthlyStatsResponse> getMonthlyStats(
            @PathVariable String regionCode,
            @PathVariable String collectMonth) {
        MonthlyStatsResponse response = monthlyStatsService.getMonthlyStats(regionCode, collectMonth);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역별 최근 6개월 트렌드 조회", description = "특정 지역의 최근 6개월 월세 트렌드를 조회합니다.")
    @GetMapping("/{regionCode}/trend")
    public ResponseEntity<TrendResponse> getRecentTrend(
            @PathVariable String regionCode) {
        TrendResponse response = monthlyStatsService.getRecentTrend(regionCode);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역명으로 통계 검색", description = "지역명을 포함하는 모든 통계 데이터를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<MonthlyStatsResponse>> searchByRegionName(
            @RequestParam String regionName) {
        List<MonthlyStatsResponse> response = monthlyStatsService.searchByRegionName(regionName);
        return ResponseEntity.ok(response);
    }
}
