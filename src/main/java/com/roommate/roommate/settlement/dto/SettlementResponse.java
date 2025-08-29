package com.roommate.roommate.settlement.dto;

import com.roommate.roommate.settlement.entity.Settlement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "정산 응답")
public class SettlementResponse {
    
    @Schema(description = "정산 ID", example = "1")
    private Long id;
    
    @Schema(description = "정산 제목", example = "2025년 1월 정산")
    private String title;
    
    @Schema(description = "총 정산 금액", example = "50000.00")
    private BigDecimal totalAmount;
    
    @Schema(description = "정산 상태", example = "PENDING")
    private Settlement.SettlementStatus status;
    
    @Schema(description = "정산 시작 날짜")
    private LocalDate startDate;
    
    @Schema(description = "정산 종료 날짜")
    private LocalDate endDate;
    
    @Schema(description = "생성자 ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;
}
