package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "평균 월세 통계 응답")
public record AvgMonthlyRentResponse(
        @Schema(description = "지역 코드", example = "11215")
        String regionCode,
        
        @Schema(description = "지역명", example = "광진구")
        String regionName,
        
        @Schema(description = "주택 유형", example = "rowhouse")
        String type,
        
        @Schema(description = "수집 월", example = "202508")
        String collectMonth,
        
        @Schema(description = "전체 평균 월세", example = "52")
        Integer avgMonthlyRent
) {}
