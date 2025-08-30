package com.roommate.roommate.settlement.entity;

import com.roommate.roommate.settlement.entity.Expense;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "settlements")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정산 내역 엔티티")
public class Settlement {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "정산 ID")
    private Long id;

    @Column(name = "space_id", nullable = false)
    @Schema(description = "소속 스페이스 ID")
    private Long spaceId;

    @Column(name = "title", nullable = false, length = 100)
    @Schema(description = "정산 제목 (자동 생성)")
    private String title;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @Schema(description = "총 정산 금액")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(description = "정산 상태")
    private SettlementStatus status;

    @Column(name = "start_date")
    @Schema(description = "정산 시작 날짜")
    private LocalDate startDate;

    @Column(name = "end_date")
    @Schema(description = "정산 종료 날짜")
    private LocalDate endDate;

    @Column(name = "created_by", nullable = false)
    @Schema(description = "생성자 ID")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "settlement", fetch = FetchType.LAZY)
    @Schema(description = "연결된 지출 목록")
    private List<Expense> expenses;

            @PrePersist
        protected void onCreate() {
            LocalDateTime now = LocalDateTime.now();
            createdAt = now;
            
            // 정산 제목 자동 생성: "2025년 1월 15일 정산"
            if (title == null) {
                title = String.format("%d년 %d월 %d일 정산", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
            }
            
            // 정산 시작 날짜 자동 설정
            if (startDate == null) {
                startDate = now.toLocalDate();
            }
        }

    public enum SettlementStatus {
        PENDING,    // 진행중
        COMPLETED   // 완료
    }
}
