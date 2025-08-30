package com.roommate.roommate.settlement.service;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.dto.SettlementDetailResponse;
import com.roommate.roommate.settlement.dto.SettlementResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.repository.ExpenseRepository;
import com.roommate.roommate.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {
    
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    
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
    
    // DTO 변환 메서드들
    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseType(expense.getExpenseType())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .itemsJson(expense.getItemsJson())
                .attachmentUrl(expense.getAttachmentUrl())
                .createdBy(expense.getCreatedBy())
                .createdAt(expense.getCreatedAt())
                .build();
    }
    
    private SettlementResponse mapToSettlementResponse(Settlement settlement) {
        return SettlementResponse.builder()
                .id(settlement.getId())
                .title(settlement.getTitle())
                .totalAmount(settlement.getTotalAmount())
                .status(settlement.getStatus())
                .startDate(settlement.getStartDate())
                .endDate(settlement.getEndDate())
                .createdBy(settlement.getCreatedBy())
                .createdAt(settlement.getCreatedAt())
                .build();
    }
}
