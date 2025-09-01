package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 트렌드 데이터")
public record MonthlyTrendData(
        @Schema(description = "수집 월", example = "202408")
        String month,
        
        @Schema(description = "청년 중앙값 월세", example = "750000")
        Integer youthMedianMonthlyRent,
        
        @Schema(description = "청년 평균 월세", example = "820000")
        Integer youthAvgMonthlyRent,
        
        @Schema(description = "전체 계약 수", example = "1500")
        Integer totalContracts
) {}
