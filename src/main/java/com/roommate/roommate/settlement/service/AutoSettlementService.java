package com.roommate.roommate.settlement.service;

import com.roommate.roommate.settlement.dto.ExpenseCreateRequest;
import com.roommate.roommate.settlement.dto.ExpenseResponse;
import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import com.roommate.roommate.settlement.repository.ExpenseRepository;
import com.roommate.roommate.settlement.repository.SettlementRepository;
import com.roommate.roommate.auth.User;
import com.roommate.roommate.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSettlementService {

    private final OcrService ocrService;
    private final AiAnalysisService aiAnalysisService;
    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;


    // 영수증 이미지로 자동 지출 생성하는 메서드
    // OCR -> AI 분석 -> DB 저장 -> 정산 연결
    @Transactional
    public ExpenseResponse createExpenseFromReceipt(MultipartFile image, Long spaceId, Long settlementId, Long userId) {
        try {
            // 1단계: OCR로 이미지에서 텍스트 추출
            byte[] imageBytes = image.getBytes();
            String ocrResult = ocrService.analyzeReceipt(imageBytes);
            
            // 2단계: OpenAI로 구조화된 데이터 추출
            AiAnalysisService.ReceiptAnalysisResult analysis = aiAnalysisService.analyzeReceiptOcr(ocrResult);
            
            // 3단계: 지출 엔티티 생성 및 저장 
            Expense expense = Expense.builder()
                    .settlement(getSettlement(settlementId))
                    .expenseType(Expense.ExpenseType.RECEIPT)
                    .category(analysis.getCategory())
                    .amount(analysis.getAmount())
                    .itemsJson(createSimpleItemsJson(analysis.getItems()))
                    .attachmentUrl(saveImage(image, spaceId, "receipt"))
                    .createdBy(userId)
                    .createdAt(analysis.getDate())
                    .build();

            Expense savedExpense = expenseRepository.save(expense);
            
            // 4단계: 정산 총 금액 업데이트
            updateSettlementTotalAmount(settlementId, savedExpense.getAmount());
            
            // User 정보 조회하여 이름 설정
            User user = userRepository.findById(userId).orElse(null);
            
            return ExpenseResponse.builder()
                    .id(savedExpense.getId())
                    .expenseType(savedExpense.getExpenseType())
                    .category(savedExpense.getCategory())
                    .amount(savedExpense.getAmount())
                    .itemsJson(savedExpense.getItemsJson())
                    .attachmentUrl(savedExpense.getAttachmentUrl())
                    .createdBy(savedExpense.getCreatedBy())
                    .createdByName(user != null ? user.getUsername() : "알 수 없음")
                    .createdAt(savedExpense.getCreatedAt())
                    .build();

        } catch (Exception e) {
            log.error("영수증 자동 지출 생성 중 오류 발생", e);
            throw new RuntimeException("영수증 자동 지출 생성에 실패했습니다.", e);
        }
    }

    // 공과금 이미지로 자동 지출 생성하는 메서드
    @Transactional
    public ExpenseResponse createExpenseFromUtility(MultipartFile image, Long spaceId, Long settlementId, Long userId) {
        try {
            // 1단계: OCR로 이미지에서 텍스트 추출
            byte[] imageBytes = image.getBytes();
            String ocrResult = ocrService.analyzeUtility(imageBytes);
            
            // 2단계: OpenAI로 구조화된 데이터 추출
            AiAnalysisService.UtilityAnalysisResult analysis = aiAnalysisService.analyzeUtilityOcr(ocrResult);
            
            // 3단계: 지출 엔티티 생성 및 저장 (공과금은 품목 정보를 null로 설정)
            Expense expense = Expense.builder()
                    .settlement(getSettlement(settlementId))
                    .expenseType(Expense.ExpenseType.UTILITY)
                    .category(analysis.getCategory())
                    .amount(analysis.getAmount())
                    .itemsJson(null) // 공과금은 품목 정보 없음
                    .attachmentUrl(saveImage(image, spaceId, "utility"))
                    .createdBy(userId)
                    .createdAt(analysis.getDate())
                    .build();

            Expense savedExpense = expenseRepository.save(expense);
            
            // 4단계: 정산 총 금액 업데이트
            updateSettlementTotalAmount(settlementId, savedExpense.getAmount());
            
            // User 정보 조회하여 이름 설정
            User user = userRepository.findById(userId).orElse(null);
            
            return ExpenseResponse.builder()
                    .id(savedExpense.getId())
                    .expenseType(savedExpense.getExpenseType())
                    .category(savedExpense.getCategory())
                    .amount(savedExpense.getAmount())
                    .itemsJson(savedExpense.getItemsJson())
                    .attachmentUrl(savedExpense.getAttachmentUrl())
                    .createdBy(savedExpense.getCreatedBy())
                    .createdByName(user != null ? user.getUsername() : "알 수 없음")
                    .createdAt(savedExpense.getCreatedAt())
                    .build();

        } catch (Exception e) {
            log.error("공과금 자동 지출 생성 중 오류 발생", e);
            throw new RuntimeException("공과금 자동 지출 생성에 실패했습니다.", e);
        }
    }

    // 이미지 파일 저장 (실제 구현에서는 파일 스토리지 서비스 사용)
    private String saveImage(MultipartFile image, Long spaceId, String type) {
        // TODO: 실제 파일 스토리지 서비스 구현 필요
        // 현재는 임시 URL 반환
        String fileName = type + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
        return "/uploads/" + spaceId + "/" + fileName;
    }

    // 정산 조회
    private Settlement getSettlement(Long settlementId) {
        return settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다."));
    }

    // 정산 총 금액 업데이트
    private void updateSettlementTotalAmount(Long settlementId, BigDecimal amount) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new RuntimeException("정산을 찾을 수 없습니다."));
        
        // 정산 총 금액 업데이트
        settlement.setTotalAmount(settlement.getTotalAmount().add(amount));
        settlementRepository.save(settlement);
        
        log.info("정산 {} 총 금액 업데이트: +{} = {}", settlementId, amount, settlement.getTotalAmount());
    }

    // 간단한 JSON 문자열 생성 (ObjectMapper 없이)
    private String createSimpleItemsJson(List<AiAnalysisService.ItemInfo> items) {
        if (items == null || items.isEmpty()) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            AiAnalysisService.ItemInfo item = items.get(i);
            if (i > 0) json.append(",");
            
            json.append("{")
                .append("\"itemName\":\"").append(item.getItemName()).append("\",")
                .append("\"unitPrice\":").append(item.getUnitPrice()).append(",")
                .append("\"quantity\":").append(item.getQuantity()).append(",")
                .append("\"itemAmount\":").append(item.getItemAmount())
                .append("}");
        }
        json.append("]");
        
        return json.toString();
    }

}
