package com.roommate.roommate.settlement.controller;

import com.roommate.roommate.settlement.dto.*;
import com.roommate.roommate.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/settlements")
@RequiredArgsConstructor
@Tag(name = "정산 관리", description = "정산 및 지출 관련 API")
public class SettlementController {
    
    private final SettlementService settlementService;
    
    // 정산 생성
    @PostMapping
    @Operation(summary = "정산 생성", description = "새로운 정산을 생성합니다.")
    public ResponseEntity<SettlementResponse> createSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        SettlementResponse response = settlementService.createSettlement(spaceId, userId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 목록 조회
    @GetMapping
    @Operation(summary = "정산 목록 조회", description = "스페이스의 정산 목록을 조회합니다.")
    public ResponseEntity<List<SettlementResponse>> getSettlements(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId) {
        List<SettlementResponse> response = settlementService.getSettlementsBySpace(spaceId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 상세 조회
    @GetMapping("/{settlementId}")
    @Operation(summary = "정산 상세 조회", description = "정산의 상세 정보를 조회합니다.")
    public ResponseEntity<SettlementDetailResponse> getSettlementDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        SettlementDetailResponse response = settlementService.getSettlementDetail(settlementId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 완료
    @PostMapping("/{settlementId}/complete")
    @Operation(summary = "정산 완료", description = "정산을 완료 상태로 변경합니다.")
    public ResponseEntity<SettlementResponse> completeSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        SettlementResponse response = settlementService.completeSettlement(settlementId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 생성
    @PostMapping("/{settlementId}/expenses")
    @Operation(summary = "지출 생성", description = "정산에 지출을 추가합니다.")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @RequestBody ExpenseCreateRequest request,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        ExpenseResponse response = settlementService.createExpense(spaceId, settlementId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 수정
    @PutMapping("/{settlementId}/expenses/{expenseId}")
    @Operation(summary = "지출 수정", description = "기존 지출을 수정합니다.")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId,
            @RequestBody ExpenseCreateRequest request,
            @Parameter(description = "사용자 ID") @RequestParam Long userId) {
        ExpenseResponse response = settlementService.updateExpense(spaceId, settlementId, expenseId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 삭제
    @DeleteMapping("/{settlementId}/expenses/{expenseId}")
    @Operation(summary = "지출 삭제", description = "지출을 삭제합니다.")
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        settlementService.deleteExpense(spaceId, settlementId, expenseId);
        return ResponseEntity.noContent().build();
    }
    
    // 정산의 지출 목록 조회
    @GetMapping("/{settlementId}/expenses")
    @Operation(summary = "지출 목록 조회", description = "정산에 포함된 지출 목록을 조회합니다.")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        List<ExpenseResponse> response = settlementService.getExpensesBySettlement(settlementId);
        return ResponseEntity.ok(response);
    }
    
    // 정산의 지출 상세 조회
    @GetMapping("/{settlementId}/expenses/{expenseId}")
    @Operation(summary = "지출 상세 조회", description = "지출의 상세 정보를 조회합니다.")
    public ResponseEntity<ExpenseResponse> getExpenseDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        ExpenseResponse response = settlementService.getExpenseDetail(settlementId, expenseId);
        return ResponseEntity.ok(response);
    }
    
    // 스페이스 멤버 조회 (정산 참여자 선택용)
    @GetMapping("/{settlementId}/members")
    @Operation(summary = "스페이스 멤버 조회", description = "정산에 참여할 수 있는 스페이스 멤버 목록을 조회합니다.")
    public ResponseEntity<List<SpaceMemberResponse>> getSettlementMembers(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        List<SpaceMemberResponse> response = settlementService.getSpaceMembers(spaceId);
        return ResponseEntity.ok(response);
    }
    
    // 정산 계산
    @PostMapping("/{settlementId}/calculate")
    @Operation(summary = "정산 계산", description = "정산을 계산하고 참여자들에게 알림을 전송합니다.")
    public ResponseEntity<SettlementCalculationResponse> calculateSettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @RequestBody SettlementCalculationRequest request) {
        SettlementCalculationResponse response = settlementService.calculateSettlement(settlementId, request);
        return ResponseEntity.ok(response);
    }
}
