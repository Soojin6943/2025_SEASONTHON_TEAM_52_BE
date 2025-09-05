package com.roommate.roommate.location.controller;

import com.roommate.roommate.location.dto.MonthlyStatsResponse;
import com.roommate.roommate.location.dto.TrendResponse;
import com.roommate.roommate.location.dto.AreaStatsResponse;
import com.roommate.roommate.location.dto.AvgMonthlyRentResponse;
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
    
    @Operation(summary = "지역별 주택유형별 최신 월세 통계 조회", description = "특정 지역과 주택유형의 최신 평균 월세를 조회합니다.")
    @GetMapping("/{regionCode}/type/{type}/latest")
    public ResponseEntity<AvgMonthlyRentResponse> getLatestMonthlyStatsByType(
            @PathVariable String regionCode,
            @PathVariable String type) {
        AvgMonthlyRentResponse response = monthlyStatsService.getLatestMonthlyStatsByType(regionCode, type);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역별 주택유형별 면적당 최신 월세 통계 조회", description = "특정 지역, 주택유형, 면적대의 최신 월세 통계를 조회합니다.")
    @GetMapping("/{regionCode}/type/{type}/latest/area")
    public ResponseEntity<AreaStatsResponse> getLatestAreaMonthlyStatsByType(
            @PathVariable String regionCode,
            @PathVariable String type,
            @RequestParam String areaRange) {
        AreaStatsResponse response = monthlyStatsService.getLatestAreaMonthlyStatsByType(regionCode, type, areaRange);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역별 주택유형별 특정 월 통계 조회", description = "특정 지역, 주택유형, 월의 통계를 조회합니다.")
    @GetMapping("/{regionCode}/type/{type}/month/{collectMonth}")
    public ResponseEntity<MonthlyStatsResponse> getMonthlyStatsByType(
            @PathVariable String regionCode,
            @PathVariable String type,
            @PathVariable String collectMonth) {
        MonthlyStatsResponse response = monthlyStatsService.getMonthlyStatsByType(regionCode, type, collectMonth);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "지역별 최근 6개월 트렌드 조회", description = "특정 지역의 최근 6개월 월세 트렌드를 조회합니다.")
    @GetMapping("/{regionCode}/trend")
    public ResponseEntity<TrendResponse> getRecentTrend(
            @PathVariable String regionCode) {
        TrendResponse response = monthlyStatsService.getRecentTrend(regionCode);
        return ResponseEntity.ok(response);
    }
}
