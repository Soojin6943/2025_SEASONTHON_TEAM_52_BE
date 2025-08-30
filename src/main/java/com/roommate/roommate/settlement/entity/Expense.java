package com.roommate.roommate.settlement.entity;

import com.roommate.roommate.settlement.entity.Settlement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "지출 내역 엔티티")
public class Expense {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "지출 ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", nullable = false)
    @Schema(description = "지출 유형")
    private ExpenseType expenseType;



    @Column(name = "category", length = 50)
    @Schema(description = "카테고리", example = "전기세")
    private String category;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @Schema(description = "지출 금액", example = "25000.00")
    private BigDecimal amount;

    // 품목 목록 (JSON 형태로 저장) - 영수증의 경우 상품 배열, 공과금의 경우 null
    @Column(name = "items_json", columnDefinition = "TEXT")
    @Schema(description = "품목 목록 JSON (영수증: 상품 배열, 공과금: null)")
    private String itemsJson;

    @Column(name = "attachment_url", length = 500)
    @Schema(description = "첨부자료 URL")
    private String attachmentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    @Schema(description = "소속 정산")
    private Settlement settlement;

    @Column(name = "created_by", nullable = false)
    @Schema(description = "작성자 ID")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ExpenseType {
        RECEIPT,    // 영수증
        UTILITY     // 공과금
    }
}
