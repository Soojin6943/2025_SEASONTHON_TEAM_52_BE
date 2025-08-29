package com.roommate.roommate.settlement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_expenses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정산-지출 연결 엔티티")
public class SettlementExpense {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "연결 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    @Schema(description = "정산")
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @Schema(description = "지출")
    private Expense expense;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
