package com.roommate.roommate.settlement.controller;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.dto.SettlementDetailResponse;
import com.roommate.roommate.settlement.dto.SettlementResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.service.SettlementService;
import com.roommate.roommate.settlement.service.AutoSettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/settlements")
@RequiredArgsConstructor
@Tag(name = "정산관리", description = "정산관리 관련 API")
public class SettlementController {
    
    private final SettlementService settlementService;
    private final AutoSettlementService autoSettlementService;
    
    // OCR을 통한 영수증 자동 지출 생성
    @PostMapping("/{settlementId}/expenses/receipt/ocr")
    @Operation(summary = "영수증 이미지로 자동 지출 생성")
    public ResponseEntity<ExpenseResponse> createExpenseFromReceiptOcr(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "영수증 이미지") @RequestParam("image") MultipartFile image,
            HttpServletRequest httpRequest) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("이미지 파일이 없습니다.");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        ExpenseResponse response = autoSettlementService.createExpenseFromReceipt(image, spaceId, settlementId, userId);
        return ResponseEntity.ok(response);
    }
    
    // OCR을 통한 공과금 자동 지출 생성
    @PostMapping("/{settlementId}/expenses/utility/ocr")
    @Operation(summary = "공과금 이미지로 자동 지출 생성")
    public ResponseEntity<ExpenseResponse> createExpenseFromUtilityOcr(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "공과금 이미지") @RequestParam("image") MultipartFile image,
            HttpServletRequest httpRequest) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("이미지 파일이 없습니다.");
        }
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        ExpenseResponse response = autoSettlementService.createExpenseFromUtility(image, spaceId, settlementId, userId);
        return ResponseEntity.ok(response);
    }
    
    // 지출 생성 (정산에 추가)
    @PostMapping("/{settlementId}/expenses")
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
    
    // 정산의 지출 목록 조회
    @GetMapping("/{settlementId}/expenses")
    @Operation(summary = "정산의 지출 목록 조회")
    public ResponseEntity<List<ExpenseResponse>> getExpensesBySettlement(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        
        List<ExpenseResponse> expenses = settlementService.getExpensesBySettlement(settlementId);
        return ResponseEntity.ok(expenses);
    }
    
    // 정산의 지출 상세 조회
    @GetMapping("/{settlementId}/expenses/{expenseId}")
    @Operation(summary = "정산의 지출 상세 조회")
    public ResponseEntity<ExpenseResponse> getExpenseDetail(
            @Parameter(description = "스페이스 ID") @PathVariable Long spaceId,
            @Parameter(description = "정산 ID") @PathVariable Long settlementId,
            @Parameter(description = "지출 ID") @PathVariable Long expenseId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (settlementId == null || settlementId <= 0) {
            throw new RuntimeException("유효하지 않은 정산 ID입니다.");
        }
        if (expenseId == null || expenseId <= 0) {
            throw new RuntimeException("유효하지 않은 지출 ID입니다.");
        }
        
        ExpenseResponse expense = settlementService.getExpenseDetail(settlementId, expenseId);
        return ResponseEntity.ok(expense);
    }
    
    // 정산 생성 (빈 정산)
    @PostMapping
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
    @GetMapping
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
    @GetMapping("/status/{status}")
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
    @GetMapping("/{settlementId}")
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
    @PostMapping("/{settlementId}/complete")
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
