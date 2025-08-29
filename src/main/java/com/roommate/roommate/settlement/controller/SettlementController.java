package com.roommate.roommate.settlement.controller;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.dto.SettlementDetailResponse;
import com.roommate.roommate.settlement.dto.SettlementResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/settlements")
@RequiredArgsConstructor
@Tag(name = "정산관리", description = "정산관리 관련 API")
public class SettlementController {
    
    private final SettlementService settlementService;
    
    // 지출 생성 (정산에 추가)
    @PostMapping("/settlements/{settlementId}/expenses")
    @Operation(summary = "정산에 지출 추가")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 생성 요청") @RequestBody ExpenseCreateRequest request,
            HttpServletRequest httpRequest) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        ExpenseResponse response = settlementService.createExpense(spaceId, settlementId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 목록 조회
    @GetMapping("/expenses")
    @Operation(summary = "지출 목록 조회")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        List<ExpenseResponse> expenses = settlementService.getExpensesBySpace(spaceId);
        return ResponseEntity.ok(expenses);
    }
    
    // 지출 유형별 조회
    @GetMapping("/expenses/type/{expenseType}")
    @Operation(summary = "지출 유형별 조회")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByType(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "지출 유형") @PathVariable Expense.ExpenseType expenseType) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (expenseType == null) {
            throw new RuntimeException("지출 유형을 입력해주세요.");
        }
        
        List<ExpenseResponse> expenses = settlementService.getExpensesByType(spaceId, expenseType);
        return ResponseEntity.ok(expenses);
    }
    
    // 지출 상세 조회
    @GetMapping("/expenses/{expenseId}")
    @Operation(summary = "지출 상세 조회")
    public ResponseEntity<ExpenseResponse> getExpenseDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        
        ExpenseResponse expense = settlementService.getExpenseDetail(spaceId, expenseId);
        return ResponseEntity.ok(expense);
    }
    
    // 정산 생성 (빈 정산)
    @PostMapping("/settlements")
    @Operation(summary = "정산 생성")
    public ResponseEntity<SettlementResponse> createSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            HttpServletRequest httpRequest) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        SettlementResponse response = settlementService.createSettlement(spaceId, userId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 목록 조회
    @GetMapping("/settlements")
    @Operation(summary = "정산 목록 조회")
    public ResponseEntity<List<SettlementResponse>> getSettlements(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        List<SettlementResponse> settlements = settlementService.getSettlementsBySpace(spaceId);
        return ResponseEntity.ok(settlements);
    }
    
    // 정산 상태별 조회
    @GetMapping("/settlements/status/{status}")
    @Operation(summary = "정산 상태별 조회")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByStatus(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 상태") @PathVariable Settlement.SettlementStatus status) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (status == null) {
            throw new RuntimeException("정산 상태를 입력해주세요.");
        }
        
        List<SettlementResponse> settlements = settlementService.getSettlementsByStatus(spaceId, status);
        return ResponseEntity.ok(settlements);
    }
    
    // 정산 상세 조회
    @GetMapping("/settlements/{settlementId}")
    @Operation(summary = "정산 상세 조회")
    public ResponseEntity<SettlementDetailResponse> getSettlementDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        SettlementDetailResponse response = settlementService.getSettlementDetail(settlementId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 완료
    @PostMapping("/settlements/{settlementId}/complete")
    @Operation(summary = "정산 완료")
    public ResponseEntity<SettlementResponse> completeSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        SettlementResponse response = settlementService.completeSettlement(settlementId);
        return ResponseEntity.ok(response);
    }
}
