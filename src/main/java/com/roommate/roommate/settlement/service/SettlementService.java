package com.roommate.roommate.settlement.service;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.dto.SettlementDetailResponse;
import com.roommate.roommate.settlement.dto.SettlementResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.entity.SettlementExpense;
import com.roommate.roommate.settlement.repository.ExpenseRepository;
import com.roommate.roommate.settlement.repository.SettlementExpenseRepository;
import com.roommate.roommate.settlement.repository.SettlementRepository;
import com.roommate.roommate.space.entity.Space;
import com.roommate.roommate.space.repository.SpaceRepository;
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
    private final SettlementExpenseRepository settlementExpenseRepository;
    private final SpaceRepository spaceRepository;
    
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
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("스페이스를 찾을 수 없습니다. (ID: " + spaceId + ")"));
        
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다. (ID: " + settlementId + ")"));
        
        // 정산이 해당 스페이스에 속하는지 확인
        if (!settlement.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("정산이 해당 스페이스에 속하지 않습니다. (정산 ID: " + settlementId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        // 지출 생성
        Expense expense = Expense.builder()
                .space(space)
                .expenseType(request.getExpenseType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .attachmentUrl(request.getAttachmentUrl())
                .createdBy(userId)
                .build();
        
        Expense saved = expenseRepository.save(expense);
        
        // 정산-지출 연결 생성
        SettlementExpense settlementExpense = SettlementExpense.builder()
                .settlement(settlement)
                .expense(saved)
                .build();
        
        settlementExpenseRepository.save(settlementExpense);
        
        // 정산 총 금액 업데이트
        settlement.setTotalAmount(settlement.getTotalAmount().add(request.getAmount()));
        
        settlementRepository.save(settlement);
        
        return mapToExpenseResponse(saved);
    }
    
    // 지출 목록 조회
    public List<ExpenseResponse> getExpensesBySpace(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<Expense> expenses = expenseRepository.findBySpaceIdOrderByCreatedAtDesc(spaceId);
        return expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    // 지출 유형별 조회
    public List<ExpenseResponse> getExpensesByType(Long spaceId, Expense.ExpenseType expenseType) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (expenseType == null) {
            throw new RuntimeException("지출 유형을 입력해주세요.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<Expense> expenses = expenseRepository.findBySpaceIdAndExpenseTypeOrderByCreatedAtDesc(spaceId, expenseType);
        return expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }
    
    // 지출 상세 조회
    public ExpenseResponse getExpenseDetail(Long spaceId, Long expenseId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("지출을 찾을 수 없습니다. (ID: " + expenseId + ")"));
        
        // 지출이 해당 스페이스에 속하는지 확인
        if (!expense.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("지출이 해당 스페이스에 속하지 않습니다. (지출 ID: " + expenseId + ", 스페이스 ID: " + spaceId + ")");
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
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("스페이스를 찾을 수 없습니다. (ID: " + spaceId + ")"));
        
        // 빈 정산 생성 (총액: 0원, 지출: 없음)
        Settlement settlement = Settlement.builder()
                .space(space)
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
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
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
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
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
        
        List<SettlementExpense> settlementExpenses = settlementExpenseRepository
                .findBySettlementIdOrderByCreatedAtAsc(settlementId);
        
        List<ExpenseResponse> expenses = settlementExpenses.stream()
                .map(se -> mapToExpenseResponse(se.getExpense()))
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
                .expenses(expenses)
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
