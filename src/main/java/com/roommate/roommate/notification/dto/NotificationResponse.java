package com.roommate.roommate.notification.dto;

import com.roommate.roommate.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 응답 DTO")
public class NotificationResponse {
    
    @Schema(description = "알림 ID")
    private Long id;

    @Schema(description = "스페이스 ID")
    private Long spaceId;

    @Schema(description = "수신자 ID")
    private Long userId;

    @Schema(description = "알림 제목")
    private String title;

    @Schema(description = "알림 내용")
    private String content;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .spaceId(notification.getSpaceId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
