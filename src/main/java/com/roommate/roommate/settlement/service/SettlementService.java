package com.roommate.roommate.settlement.service;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.dto.SettlementDetailResponse;
import com.roommate.roommate.settlement.dto.SettlementResponse;
import com.roommate.roommate.settlement.dto.SettlementCalculationRequest;
import com.roommate.roommate.settlement.dto.SettlementCalculationResponse;
import com.roommate.roommate.settlement.dto.SpaceMemberResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.repository.ExpenseRepository;
import com.roommate.roommate.settlement.repository.SettlementRepository;
import com.roommate.roommate.auth.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.space.entity.SpaceMember;
import com.roommate.roommate.space.repository.SpaceMemberRepository;
import com.roommate.roommate.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {
    
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final NotificationService notificationService;
    
    // 지출 생성 (정산에 추가)
    @Transactional
    public ExpenseResponse createExpense(Long spaceId, Long settlementId, ExpenseCreateRequest request, Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        // 정산이 해당 스페이스에 속하는지 확인
        if (!settlement.getSpaceId().equals(spaceId)) {
            throw new RuntimeException("정산이 해당 스페이스에 속하지 않습니다. (정산 ID: " + settlementId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        // 지출 생성
        Expense expense = Expense.builder()
                .settlement(settlement)
                .expenseType(request.getExpenseType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .itemsJson(request.getItems())
                .attachmentUrl(request.getAttachmentUrl())
                .createdBy(userId)
                .build();
        
        Expense saved = expenseRepository.save(expense);
        
        // 정산 총 금액 업데이트
        settlement.setTotalAmount(settlement.getTotalAmount().add(request.getAmount()));
        settlementRepository.save(settlement);
        
        System.out.println("지출 생성 완료: 정산ID=" + settlementId + ", 지출ID=" + saved.getId() + ", 금액=" + request.getAmount());
        
        return mapToExpenseResponse(saved);
    }
    
    // 지출 수정
    @Transactional
    public ExpenseResponse updateExpense(Long spaceId, Long settlementId, Long expenseId, ExpenseCreateRequest request, Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        // 정산이 해당 스페이스에 속하는지 확인
        if (!settlement.getSpaceId().equals(spaceId)) {
            throw new RuntimeException("정산이 해당 스페이스에 속하지 않습니다. (정산 ID: " + settlementId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출을 찾을 수 없습니다. (ID: " + expenseId + ")"));
        
        // 지출이 해당 정산에 속하는지 확인
        if (!expense.getSettlement().getId().equals(settlementId)) {
            throw new RuntimeException("지출이 해당 정산에 속하지 않습니다. (지출 ID: " + expenseId + ", 정산 ID: " + settlementId + ")");
        }
        
        // 기존 금액을 빼고 새 금액을 더함
        BigDecimal oldAmount = expense.getAmount();
        BigDecimal newAmount = request.getAmount();
        
        // 정산 총 금액 업데이트 (기존 금액 제거 + 새 금액 추가)
        settlement.setTotalAmount(settlement.getTotalAmount().subtract(oldAmount).add(newAmount));
        
        // 지출 정보 업데이트
        expense.setExpenseType(request.getExpenseType());
        expense.setCategory(request.getCategory());
        expense.setAmount(newAmount);
        expense.setItemsJson(request.getItems());
        expense.setAttachmentUrl(request.getAttachmentUrl());
        
        expenseRepository.save(expense);
        settlementRepository.save(settlement);
        
        System.out.println("지출 수정 완료: 정산ID=" + settlementId + ", 지출ID=" + expenseId + ", 기존금액=" + oldAmount + ", 새금액=" + newAmount);
        
        return mapToExpenseResponse(expense);
    }
    
    // 지출 삭제
    @Transactional
    public void deleteExpense(Long spaceId, Long settlementId, Long expenseId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        // 정산이 해당 스페이스에 속하는지 확인
        if (!settlement.getSpaceId().equals(spaceId)) {
            throw new RuntimeException("정산이 해당 스페이스에 속하지 않습니다. (정산 ID: " + settlementId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출을 찾을 수 없습니다. (ID: " + expenseId + ")"));
        
        // 지출이 해당 정산에 속하는지 확인
        if (!expense.getSettlement().getId().equals(settlementId)) {
            throw new RuntimeException("지출이 해당 정산에 속하지 않습니다. (지출 ID: " + expenseId + ", 정산 ID: " + settlementId + ")");
        }
        
        // 정산 총 금액에서 삭제할 지출 금액을 뺌
        BigDecimal deletedAmount = expense.getAmount();
        settlement.setTotalAmount(settlement.getTotalAmount().subtract(deletedAmount));
        
        // 지출 삭제
        expenseRepository.delete(expense);
        settlementRepository.save(settlement);
        
        System.out.println("지출 삭제 완료: 정산ID=" + settlementId + ", 지출ID=" + expenseId + ", 삭제금액=" + deletedAmount);
    }
    

    
    // 정산의 지출 목록 조회
    public List<ExpenseResponse> getExpensesBySettlement(Long settlementId) {
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        // 정산 존재 여부 확인
        if (!settlementRepository.existsById(settlementId)) {
            throw new RuntimeException("존재하지 않는 정산입니다. (ID: " + settlementId + ")");
        }
        
        // Settlement 엔티티에서 직접 조회
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        if (settlement.getExpenses() == null) {
            return List.of();
        }
        
        return settlement.getExpenses().stream()
                .sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt()))
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    // 정산의 지출 상세 조회
    public ExpenseResponse getExpenseDetail(Long settlementId, Long expenseId) {
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출을 찾을 수 없습니다. (ID: " + expenseId + ")"));
        
        // 지출이 해당 정산에 속하는지 확인
        if (!expense.getSettlement().getId().equals(settlementId)) {
            throw new RuntimeException("지출이 해당 정산에 속하지 않습니다. (지출 ID: " + expenseId + ", 정산 ID: " + settlementId + ")");
        }
        
        return mapToExpenseResponse(expense);
    }
    

    
    // 정산 생성 (빈 정산)
    @Transactional
    public SettlementResponse createSettlement(Long spaceId, Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        // 빈 정산 생성 (총액: 0원, 지출: 없음)
        Settlement settlement = Settlement.builder()
                .spaceId(spaceId)  // spaceId만 설정
                .totalAmount(BigDecimal.ZERO)  // 초기 총액: 0원
                .status(Settlement.SettlementStatus.PENDING)
                .createdBy(userId)
                .build();
        
        Settlement savedSettlement = settlementRepository.save(settlement);
        
        return mapToSettlementResponse(savedSettlement);
    }
    
    // 정산 목록 조회
    public List<SettlementResponse> getSettlementsBySpace(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        List<Settlement> settlements = settlementRepository.findBySpaceIdOrderByCreatedAtDesc(spaceId);
        return settlements.stream()
                .map(this::mapToSettlementResponse)
                .collect(Collectors.toList());
    }
    
    // 정산 상태별 조회
    public List<SettlementResponse> getSettlementsByStatus(Long spaceId, Settlement.SettlementStatus status) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (status == null) {
            throw new RuntimeException("정산 상태를 입력해주세요.");
        }
        
        List<Settlement> settlements = settlementRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, status);
        return settlements.stream()
                .map(this::mapToSettlementResponse)
                .collect(Collectors.toList());
    }
    
    // 정산 상세 조회
    public SettlementDetailResponse getSettlementDetail(Long settlementId) {
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        System.out.println("정산 조회: " + settlement.getId() + ", 제목: " + settlement.getTitle());
        
        // User 정보 조회하여 이름 설정
        User user = userRepository.findById(settlement.getCreatedBy()).orElse(null);
        
        // Settlement 엔티티에서 직접 조회
        if (settlement.getExpenses() == null) {
            return SettlementDetailResponse.builder()
                    .id(settlement.getId())
                    .title(settlement.getTitle())
                    .totalAmount(settlement.getTotalAmount())
                    .status(settlement.getStatus())
                    .startDate(settlement.getStartDate())
                    .endDate(settlement.getEndDate())
                    .createdBy(settlement.getCreatedBy())
                    .createdByName(user != null ? user.getUsername() : "알 수 없음")
                    .createdAt(settlement.getCreatedAt())
                    .expenses(List.of())
                    .build();
        }
        
        List<ExpenseResponse> expenseResponses = settlement.getExpenses().stream()
                .sorted((e1, e2) -> e1.getCreatedAt().compareTo(e2.getCreatedAt()))
                .map(expense -> {
                    System.out.println("지출 정보: ID=" + expense.getId() + ", 카테고리=" + expense.getCategory() + ", 금액=" + expense.getAmount());
                    return mapToExpenseResponse(expense);
                })
                .collect(Collectors.toList());
        
        return SettlementDetailResponse.builder()
                .id(settlement.getId())
                .title(settlement.getTitle())
                .totalAmount(settlement.getTotalAmount())
                .status(settlement.getStatus())
                .startDate(settlement.getStartDate())
                .endDate(settlement.getEndDate())
                .createdBy(settlement.getCreatedBy())
                .createdByName(user != null ? user.getUsername() : "알 수 없음")
                .createdAt(settlement.getCreatedAt())
                .expenses(expenseResponses)
                .build();
    }
    
    // 정산 완료
    @Transactional
    public SettlementResponse completeSettlement(Long settlementId) {
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        settlement.setStatus(Settlement.SettlementStatus.COMPLETED);
        settlement.setEndDate(LocalDate.now());
        
        return mapToSettlementResponse(settlement);
    }

    // 스페이스 멤버 조회
    public List<SpaceMemberResponse> getSpaceMembers(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        return spaceMemberRepository.findBySpaceId(spaceId).stream()
                .map(this::mapToSpaceMemberResponse)
                .collect(Collectors.toList());
    }

    // 정산 계산
    @Transactional
    public SettlementCalculationResponse calculateSettlement(Long settlementId, SettlementCalculationRequest request) {
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (request == null || request.getParticipantUserIds() == null || request.getParticipantUserIds().isEmpty()) {
            throw new RuntimeException("참여자 목록을 입력해주세요.");
        }

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));

        // 정산 상태가 PENDING이 아니면 계산 불가
        if (settlement.getStatus() != Settlement.SettlementStatus.PENDING) {
            throw new RuntimeException("정산이 진행 중이 아닙니다.");
        }

        // 참여자 수
        int participantCount = request.getParticipantUserIds().size();
        
        // 총 정산 금액
        BigDecimal totalAmount = settlement.getTotalAmount();
        
        // 1인당 부담 금액 (총액을 참여자 수로 나눔)
        BigDecimal amountPerPerson = totalAmount.divide(BigDecimal.valueOf(participantCount), 0, RoundingMode.HALF_UP);
        
        // 각 참여자별 지출 금액 계산
        Map<Long, BigDecimal> memberExpenses = new HashMap<>();
        for (Long userId : request.getParticipantUserIds()) {
            BigDecimal totalExpense = expenseRepository.findBySettlementAndCreatedBy(settlement, userId)
                    .stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            memberExpenses.put(userId, totalExpense);
        }

        // 각 참여자별 정산 내역 계산
        List<SettlementCalculationResponse.ParticipantSettlement> participants = new ArrayList<>();
        
        for (Long userId : request.getParticipantUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));
            
            BigDecimal userExpenseAmount = memberExpenses.getOrDefault(userId, BigDecimal.ZERO);
            
            // 각자가 부담해야 할 금액 = 1인당 부담 금액 - 이미 지출한 금액
            BigDecimal amountToPay = amountPerPerson.subtract(userExpenseAmount);
            
            participants.add(SettlementCalculationResponse.ParticipantSettlement.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .totalExpenseAmount(userExpenseAmount)
                    .amountToPay(amountToPay)
                    .build());
        }

        SettlementCalculationResponse response = SettlementCalculationResponse.builder()
                .settlementId(settlementId)
                .spaceId(settlement.getSpaceId())
                .title(settlement.getTitle())
                .totalAmount(totalAmount)
                .participants(participants)
                .participantCount(participantCount)
                .amountPerPerson(amountPerPerson)
                .build();
        
        // 정산 계산 결과를 바탕으로 참여자들에게 알림 전송
        notificationService.sendSettlementCalculationNotifications(response);
        
        // 정산 상태를 완료로 변경
        settlement.setStatus(Settlement.SettlementStatus.COMPLETED);
        settlement.setEndDate(LocalDate.now());
        settlementRepository.save(settlement);
        
        return response;
    }
    
    // DTO 변환 메서드들
    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        // User 정보 조회하여 이름 설정
        User user = userRepository.findById(expense.getCreatedBy()).orElse(null);
        
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseType(expense.getExpenseType())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .itemsJson(expense.getItemsJson())
                .attachmentUrl(expense.getAttachmentUrl())
                .createdBy(expense.getCreatedBy())
                .createdByName(user != null ? user.getUsername() : "알 수 없음")
                .createdAt(expense.getCreatedAt())
                .build();
    }
    
    private SettlementResponse mapToSettlementResponse(Settlement settlement) {
        // User 정보 조회하여 이름 설정
        User user = userRepository.findById(settlement.getCreatedBy()).orElse(null);
        
        return SettlementResponse.builder()
                .id(settlement.getId())
                .title(settlement.getTitle())
                .totalAmount(settlement.getTotalAmount())
                .status(settlement.getStatus())
                .startDate(settlement.getStartDate())
                .endDate(settlement.getEndDate())
                .createdBy(settlement.getCreatedBy())
                .createdByName(user != null ? user.getUsername() : "알 수 없음")
                .createdAt(settlement.getCreatedAt())
                .build();
    }
    
    private SpaceMemberResponse mapToSpaceMemberResponse(SpaceMember spaceMember) {
        User user = userRepository.findById(spaceMember.getUserId()).orElse(null);
        
        return SpaceMemberResponse.builder()
                .userId(spaceMember.getUserId())
                .username(user != null ? user.getUsername() : "알 수 없음")
                .role(spaceMember.getRole().getDescription())
                .joinedAt(spaceMember.getJoinedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }
}
