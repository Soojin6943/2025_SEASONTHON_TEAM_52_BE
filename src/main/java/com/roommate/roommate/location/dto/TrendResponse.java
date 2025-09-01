package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "6개월 트렌드 응답")
public record TrendResponse(
        @Schema(description = "지역 코드", example = "11590")
        String regionCode,
        
        @Schema(description = "지역명", example = "영등포구")
        String regionName,
        
        @Schema(description = "월별 트렌드 데이터")
        List<MonthlyTrendData> trendData
) {}
