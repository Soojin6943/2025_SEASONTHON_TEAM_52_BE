package com.roommate.roommate.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "면적별 상세 정보")
public record AreaDetailResponse(
        @Schema(description = "20~25㎡ 정보")
        AreaInfo area2025,
        
        @Schema(description = "25~30㎡ 정보")
        AreaInfo area2530,
        
        @Schema(description = "30~35㎡ 정보")
        AreaInfo area3035,
        
        @Schema(description = "35~40㎡ 정보")
        AreaInfo area3540
) {}
