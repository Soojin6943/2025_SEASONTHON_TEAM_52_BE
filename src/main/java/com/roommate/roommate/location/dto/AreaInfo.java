package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면적별 정보")
public record AreaInfo(
        @Schema(description = "계약 수", example = "200")
        Integer count,
        
        @Schema(description = "중앙값 월세", example = "750000")
        Integer median,
        
        @Schema(description = "평균 월세", example = "820000")
        Integer avg
) {}
