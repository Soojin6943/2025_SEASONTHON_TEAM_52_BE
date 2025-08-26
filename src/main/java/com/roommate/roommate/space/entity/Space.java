package com.roommate.roommate.space.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "spaces")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "스페이스 엔티티")
public class Space {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "스페이스 ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "스페이스 이름", example = "우리집")
    private String name;

    @Column(name = "max_members", nullable = false)
    @Builder.Default
    @Schema(description = "최대 멤버 수", example = "4")
    private Integer maxMembers = 4;

    @Column(name = "invite_code", length = 10, unique = true)
    @Schema(description = "초대 코드", example = "ABC12345")
    private String inviteCode;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
