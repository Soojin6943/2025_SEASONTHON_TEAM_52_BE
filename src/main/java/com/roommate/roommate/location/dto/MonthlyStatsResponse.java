package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 통계 응답")
public record MonthlyStatsResponse(
        @Schema(description = "지역 코드", example = "11590")
        String regionCode,
        
        @Schema(description = "지역명", example = "영등포구")
        String regionName,
        
        @Schema(description = "수집 월", example = "202408")
        String collectMonth,
        
        @Schema(description = "전체 계약 수", example = "1500")
        Integer totalContracts,
        
        @Schema(description = "청년 계약 수", example = "800")
        Integer youthContracts,
        
        @Schema(description = "청년 중앙값 월세", example = "750000")
        Integer youthMedianMonthlyRent,
        
        @Schema(description = "청년 평균 월세", example = "820000")
        Integer youthAvgMonthlyRent,
        
        @Schema(description = "면적별 상세 정보")
        AreaDetailResponse areaDetail
) {}
