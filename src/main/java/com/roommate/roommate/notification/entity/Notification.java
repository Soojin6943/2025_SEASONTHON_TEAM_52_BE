package com.roommate.roommate.notification.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "알림 엔티티")
public class Notification {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "알림 ID")
    private Long id;

    @Column(name = "space_id", nullable = false)
    @Schema(description = "소속 스페이스 ID")
    private Long spaceId;

    @Column(name = "user_id", nullable = false)
    @Schema(description = "수신자 ID")
    private Long userId;

    @Column(name = "title", nullable = false, length = 200)
    @Schema(description = "알림 제목")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Schema(description = "알림 내용")
    private String content;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
