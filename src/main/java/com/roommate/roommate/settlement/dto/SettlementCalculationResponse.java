package com.roommate.roommate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 계산 결과 DTO")
public class SettlementCalculationResponse {
    
    @Schema(description = "정산 ID")
    private Long settlementId;
    
    @Schema(description = "스페이스 ID")
    private Long spaceId;
    
    @Schema(description = "정산 제목")
    private String title;
    
    @Schema(description = "총 정산 금액")
    private BigDecimal totalAmount;
    
    @Schema(description = "참여자별 정산 내역")
    private List<ParticipantSettlement> participants;
    
    @Schema(description = "정산 참여자 수")
    private Integer participantCount;
    
    @Schema(description = "1인당 부담 금액 (본인 지출 제외)")
    private BigDecimal amountPerPerson;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "참여자별 정산 내역")
    public static class ParticipantSettlement {
        
        @Schema(description = "사용자 ID")
        private Long userId;
        
        @Schema(description = "사용자 이름")
        private String username;
        
        @Schema(description = "본인이 지출한 총 금액")
        private BigDecimal totalExpenseAmount;
        
        @Schema(description = "본인이 부담해야 할 금액 (음수면 받아야 할 금액)")
        private BigDecimal amountToPay;
    }
}
