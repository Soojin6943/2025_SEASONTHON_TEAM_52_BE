package com.roommate.roommate.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "알림 생성 요청 DTO")
public class NotificationCreateRequest {
    
    @Schema(description = "수신자 ID")
    private Long userId;

    @Schema(description = "알림 제목")
    private String title;

    @Schema(description = "알림 내용")
    private String content;
}
