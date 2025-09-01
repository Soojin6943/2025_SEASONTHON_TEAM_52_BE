package com.roommate.roommate.notification.service;

import com.roommate.roommate.notification.dto.NotificationCreateRequest;
import com.roommate.roommate.notification.dto.NotificationResponse;
import com.roommate.roommate.notification.entity.Notification;
import com.roommate.roommate.notification.repository.NotificationRepository;
import com.roommate.roommate.settlement.dto.SettlementCalculationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    // 개인 알림 생성
    @Transactional
    public NotificationResponse createNotification(Long spaceId, NotificationCreateRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        Notification notification = Notification.builder()
                .spaceId(spaceId)
                .userId(request.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(savedNotification);
    }
    
    // 정산 계산 결과 개인별 알림 전송
    @Transactional
    public List<NotificationResponse> sendSettlementCalculationNotifications(SettlementCalculationResponse calculation) {
        List<NotificationResponse> notifications = new ArrayList<>();
        
        for (SettlementCalculationResponse.ParticipantSettlement participant : calculation.getParticipants()) {
            String title = "정산 알림";
            String content = generateSimpleSettlementContent(calculation, participant);
            
            NotificationCreateRequest request = NotificationCreateRequest.builder()
                    .userId(participant.getUserId())
                    .title(title)
                    .content(content)
                    .build();
            
            NotificationResponse notification = createNotification(calculation.getSpaceId(), request);
            notifications.add(notification);
        }
        
        return notifications;
    }
    
    // 간단한 정산 내용 생성
    private String generateSimpleSettlementContent(SettlementCalculationResponse calculation, SettlementCalculationResponse.ParticipantSettlement participant) {
        StringBuilder content = new StringBuilder();
        content.append(calculation.getTitle()).append("\n\n");
        
        // 모든 경우에 금액을 표시 (받아야 할 금액이어도 0원으로 표시)
        if (participant.getAmountToPay().compareTo(BigDecimal.ZERO) >= 0) {
            content.append("내야 할 금액: ").append(participant.getAmountToPay()).append("원");
        } else {
            // 받아야 할 금액이 있는 경우 0원으로 표시
            content.append("내야 할 금액: 0원");
        }
        
        return content.toString();
    }
    
    // 사용자별 알림 조회
    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
