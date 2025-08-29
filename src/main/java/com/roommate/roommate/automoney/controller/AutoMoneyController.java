package com.roommate.roommate.automoney.controller;

import com.roommate.roommate.automoney.dto.ExpenseCreateRequest;
import com.roommate.roommate.automoney.dto.ExpenseResponse;
import com.roommate.roommate.automoney.dto.SettlementDetailResponse;
import com.roommate.roommate.automoney.dto.SettlementResponse;
import com.roommate.roommate.automoney.entity.Expense;
import com.roommate.roommate.automoney.entity.Settlement;
import com.roommate.roommate.automoney.service.AutoMoneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/automoney")
@RequiredArgsConstructor
@Tag(name = "자동정산", description = "자동정산 관련 API")
public class AutoMoneyController {
    
    private final AutoMoneyService autoMoneyService;
    
    // 지출 생성 (정산에 추가)
    @PostMapping("/settlements/{settlementId}/expenses")
    @Operation(summary = "정산에 지출 추가")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 생성 요청") @RequestBody ExpenseCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ExpenseResponse response = autoMoneyService.createExpense(spaceId, settlementId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 목록 조회
    @GetMapping("/expenses")
    @Operation(summary = "지출 목록 조회")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId) {
        List<ExpenseResponse> expenses = autoMoneyService.getExpensesBySpace(spaceId);
        return ResponseEntity.ok(expenses);
    }
    
    // 지출 유형별 조회
    @GetMapping("/expenses/type/{expenseType}")
    @Operation(summary = "지출 유형별 조회")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByType(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "지출 유형") @PathVariable Expense.ExpenseType expenseType) {
        List<ExpenseResponse> expenses = autoMoneyService.getExpensesByType(spaceId, expenseType);
        return ResponseEntity.ok(expenses);
    }
    
    // 지출 상세 조회
    @GetMapping("/expenses/{expenseId}")
    @Operation(summary = "지출 상세 조회")
    public ResponseEntity<ExpenseResponse> getExpenseDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        ExpenseResponse expense = autoMoneyService.getExpenseDetail(spaceId, expenseId);
        return ResponseEntity.ok(expense);
    }
    

    
    // 정산 생성 (빈 정산)
    @PostMapping("/settlements")
    @Operation(summary = "정산 생성")
    public ResponseEntity<SettlementResponse> createSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        SettlementResponse response = autoMoneyService.createSettlement(spaceId, userId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 목록 조회
    @GetMapping("/settlements")
    @Operation(summary = "정산 목록 조회")
    public ResponseEntity<List<SettlementResponse>> getSettlements(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId) {
        List<SettlementResponse> settlements = autoMoneyService.getSettlementsBySpace(spaceId);
        return ResponseEntity.ok(settlements);
    }
    
    // 정산 상태별 조회
    @GetMapping("/settlements/status/{status}")
    @Operation(summary = "정산 상태별 조회")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByStatus(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 상태") @PathVariable Settlement.SettlementStatus status) {
        List<SettlementResponse> settlements = autoMoneyService.getSettlementsByStatus(spaceId, status);
        return ResponseEntity.ok(settlements);
    }
    
    // 정산 상세 조회
    @GetMapping("/settlements/{settlementId}")
    @Operation(summary = "정산 상세 조회")
    public ResponseEntity<SettlementDetailResponse> getSettlementDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        SettlementDetailResponse response = autoMoneyService.getSettlementDetail(settlementId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 완료
    @PostMapping("/settlements/{settlementId}/complete")
    @Operation(summary = "정산 완료")
    public ResponseEntity<SettlementResponse> completeSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        SettlementResponse response = autoMoneyService.completeSettlement(settlementId);
        return ResponseEntity.ok(response);
    }
}
