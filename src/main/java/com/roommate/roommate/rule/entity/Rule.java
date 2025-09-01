package com.roommate.roommate.rule.entity;

import com.roommate.roommate.space.entity.Space;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;
    
    @Column(nullable = false)
    private String content; // 규칙 내용 (예: "집청소 당번: 김태희")
    
    @ElementCollection(targetClass = DayOfWeek.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "rule_weekdays", joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "weekday")
    private Set<DayOfWeek> weekdays; // 선택된 요일들 (MONDAY, TUESDAY, ...)
    
    @Column(nullable = false)
    private Integer weekInterval; // 주기 (1주, 2주, 3주, 4주)
    
    @Column(nullable = false)
    private LocalDate startDate; // 시작 날짜
    
    @Column
    private LocalDate endDate; // 종료 날짜 (null이면 무제한)
    
    @Column(nullable = false)
    private Long createdBy; // 작성자 ID
    
    @Column(nullable = false)
    private LocalDate createdAt;
    
    @Column(nullable = false)
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
