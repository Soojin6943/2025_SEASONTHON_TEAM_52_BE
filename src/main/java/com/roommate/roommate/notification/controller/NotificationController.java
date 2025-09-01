package com.roommate.roommate.notification.controller;

import com.roommate.roommate.notification.dto.NotificationCreateRequest;
import com.roommate.roommate.notification.dto.NotificationResponse;
import com.roommate.roommate.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = "알림 관련 API")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // 사용자별 알림 목록 조회
    @GetMapping("/user")
    @Operation(summary = "사용자별 알림 목록 조회", description = "현재 로그인한 사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    // 개인 알림 생성
    @PostMapping
    @Operation(summary = "개인 알림 생성", description = "특정 사용자에게 개인 알림을 생성합니다.")
    public ResponseEntity<NotificationResponse> createNotification(
            @Parameter(description = "알림 생성 요청") @RequestBody NotificationCreateRequest request,
            @Parameter(description = "스페이스 ID") @RequestParam Long spaceId) {
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        NotificationResponse notification = notificationService.createNotification(spaceId, request);
        return ResponseEntity.ok(notification);
    }
}
