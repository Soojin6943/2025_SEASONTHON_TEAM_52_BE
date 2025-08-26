package com.roommate.roommate.calendar.entity;

import com.roommate.roommate.space.entity.Space;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shared_calendars")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공유캘린더 엔티티")
public class SharedCalendar {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "일정 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    @Schema(description = "소속 스페이스")
    private Space space;

    @Column(name = "title", nullable = false, length = 100)
    @Schema(description = "일정 제목", example = "팀 미팅")
    private String title;

    @Column(name = "content", length = 500)
    @Schema(description = "일정 내용", example = "프로젝트 진행상황 논의")
    private String content;

    @Column(name = "date", nullable = false)
    @Schema(description = "일정 날짜", example = "2025-01-15")
    private LocalDate date;

    @Column(name = "created_by", nullable = false)
    @Schema(description = "생성자 ID", example = "1")
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
