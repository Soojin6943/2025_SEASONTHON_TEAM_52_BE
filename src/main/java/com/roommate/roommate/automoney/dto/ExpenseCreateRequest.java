package com.roommate.roommate.automoney.dto;

import com.roommate.roommate.automoney.entity.Expense;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지출 생성 요청")
public class ExpenseCreateRequest {
    
    @Schema(description = "지출 유형", example = "UTILITY")
    private Expense.ExpenseType expenseType;
    
    @Schema(description = "카테고리", example = "전기세")
    private String category;
    

    
    @Schema(description = "지출 금액", example = "25000.00")
    private BigDecimal amount;
    
    @Schema(description = "첨부자료 URL")
    private String attachmentUrl;
}
