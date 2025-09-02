package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면적별 통계 응답")
public record AreaStatsResponse(
        @Schema(description = "지역 코드", example = "11215")
        String regionCode,
        
        @Schema(description = "지역명", example = "광진구")
        String regionName,
        
        @Schema(description = "주택 유형", example = "rowhouse")
        String type,
        
        @Schema(description = "수집 월", example = "202508")
        String collectMonth,
        
        @Schema(description = "면적대", example = "2530")
        String areaRange,
        
        @Schema(description = "면적별 정보")
        AreaInfo areaInfo
) {}
