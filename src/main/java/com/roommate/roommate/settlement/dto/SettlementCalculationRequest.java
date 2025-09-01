package com.roommate.roommate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 계산 요청 DTO")
public class SettlementCalculationRequest {
    
    @Schema(description = "정산에 참여할 스페이스 멤버 ID 목록", example = "[1, 2, 3]")
    private List<Long> participantUserIds;
}
