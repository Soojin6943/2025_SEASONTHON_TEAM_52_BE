package com.roommate.roommate.settlement.dto;

import com.roommate.roommate.settlement.entity.Expense;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지출 응답")
public class ExpenseResponse {
    
    @Schema(description = "지출 ID", example = "1")
    private Long id;
    
    @Schema(description = "지출 유형", example = "UTILITY")
    private Expense.ExpenseType expenseType;
    

    
    @Schema(description = "카테고리", example = "전기세")
    private String category;
    
    @Schema(description = "지출 금액", example = "25000.00")
    private BigDecimal amount;
    
    @Schema(description = "품목 목록 JSON (영수증: 상품 배열, 공과금: null)")
    private String itemsJson;
    
    @Schema(description = "첨부자료 URL")
    private String attachmentUrl;
    
    @Schema(description = "작성자 ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "작성자 이름", example = "두둥탁")
    private String createdByName;
    
    @Schema(description = "생성 날짜")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
