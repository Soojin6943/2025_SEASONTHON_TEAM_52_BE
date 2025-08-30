package com.roommate.roommate.settlement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAnalysisService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.model:gpt-4}")
    private String openaiModel;

    private final ObjectMapper objectMapper;

    /**
     * 영수증 OCR 결과를 AI로 분석하여 구조화된 데이터로 변환
     */
    public ReceiptAnalysisResult analyzeReceiptOcr(String ocrResult) {
        try {
            String prompt = createReceiptPrompt(ocrResult);
            String aiResponse = callOpenAI(prompt);
            
            return parseReceiptResponse(aiResponse);
            
        } catch (Exception e) {
            log.error("영수증 AI 분석 중 오류 발생", e);
            throw new RuntimeException("영수증 AI 분석에 실패했습니다.", e);
        }
    }

    /**
     * 공과금 OCR 결과를 AI로 분석하여 구조화된 데이터로 변환
     */
    public UtilityAnalysisResult analyzeUtilityOcr(String ocrResult) {
        try {
            String prompt = createUtilityPrompt(ocrResult);
            String aiResponse = callOpenAI(prompt);
            
            return parseUtilityResponse(aiResponse);
            
        } catch (Exception e) {
            log.error("공과금 AI 분석 중 오류 발생", e);
            throw new RuntimeException("공과금 AI 분석에 실패했습니다.", e);
        }
    }

    private String createReceiptPrompt(String ocrResult) {
        return String.format("""
            당신은 영수증 OCR 결과를 분석하는 전문가입니다. 
            다음 OCR 결과를 분석하여 정확한 JSON 형태로 반환해주세요.
            
            OCR 결과:
            %s
            
            다음 JSON 형식으로만 반환해주세요 (설명이나 다른 텍스트 없이):
            {
                "amount": "총 금액 (숫자만, 콤마나 원 표시 제거)",
                "date": "날짜 (YYYY-MM-DD 형식, OCR에서 추출한 날짜)",
                "category": "카테고리 (식비, 생활용품, 교통비, 의료비, 교육비, 기타 중에서 선택)",
                "items": [
                    {
                        "itemName": "품목명",
                        "unitPrice": "단가 (숫자만, 콤마나 원 표시 제거)",
                        "quantity": "수량 (숫자만)",
                        "itemAmount": "품목별 금액 (숫자만, 콤마나 원 표시 제거)"
                    }
                ]
            }
            
            중요 규칙:
            1. 금액은 숫자만 추출 (예: "25,000원" → "25000")
            2. 날짜는 YYYY-MM-DD 형식으로 변환
            3. 카테고리는 제공된 옵션 중에서 선택
            4. items 배열에 모든 품목을 포함 (최소 1개, 최대 10개)
            5. 품목 정보가 없는 경우 빈 배열로 설정
            6. JSON만 반환하고 다른 설명은 포함하지 마세요
            """, ocrResult);
    }

    private String createUtilityPrompt(String ocrResult) {
        return String.format("""
            당신은 공과금 OCR 결과를 분석하는 전문가입니다.
            다음 OCR 결과를 분석하여 정확한 JSON 형태로 반환해주세요.
            
            OCR 결과:
            %s
            
            다음 JSON 형식으로만 반환해주세요 (설명이나 다른 텍스트 없이):
            {
                "amount": "금액 (숫자만, 콤마나 원 표시 제거)",
                "date": "날짜 (YYYY-MM-DD 형식, OCR에서 추출한 날짜)",
                "category": "카테고리 (전기세, 수도세, 가스비, 인터넷비, 관리비, 기타 중에서 선택)"
            }
            
            중요 규칙:
            1. 금액은 숫자만 추출 (예: "25,000원" → "25000")
            2. 날짜는 YYYY-MM-DD 형식으로 변환
            3. 카테고리는 제공된 옵션 중에서 선택
            4. JSON만 반환하고 다른 설명은 포함하지 마세요
            """, ocrResult);
    }

    private String callOpenAI(String prompt) {
        try {
            OpenAiService service = new OpenAiService(openaiApiKey);
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(openaiModel)
                    .messages(List.of(new ChatMessage("user", prompt)))
                    .maxTokens(1000)
                    .temperature(0.1)
                    .build();

            String response = service.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            log.info("OpenAI 응답: {}", response);
            return response;

        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            throw new RuntimeException("OpenAI API 호출에 실패했습니다.", e);
        }
    }

    private ReceiptAnalysisResult parseReceiptResponse(String aiResponse) {
        try {
            // JSON 부분만 추출 (```json ``` 블록이 있는 경우)
            String jsonContent = aiResponse;
            if (aiResponse.contains("```json")) {
                jsonContent = aiResponse.substring(
                    aiResponse.indexOf("```json") + 7,
                    aiResponse.lastIndexOf("```")
                ).trim();
            }

            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            
            // items 배열 파싱
            List<ItemInfo> items = new ArrayList<>();
            if (jsonNode.has("items") && jsonNode.get("items").isArray()) {
                for (JsonNode itemNode : jsonNode.get("items")) {
                    ItemInfo item = ItemInfo.builder()
                            .itemName(itemNode.get("itemName").asText())
                            .unitPrice(new BigDecimal(itemNode.get("unitPrice").asText()))
                            .quantity(itemNode.get("quantity").asInt())
                            .itemAmount(new BigDecimal(itemNode.get("itemAmount").asText()))
                            .build();
                    items.add(item);
                }
            }
            
            return ReceiptAnalysisResult.builder()
                    .amount(new BigDecimal(jsonNode.get("amount").asText()))
                    .date(LocalDateTime.parse(jsonNode.get("date").asText() + "T00:00:00"))
                    .category(jsonNode.get("category").asText())
                    .items(items)
                    .rawOcrData(aiResponse)
                    .build();

        } catch (Exception e) {
            log.error("영수증 AI 응답 파싱 중 오류 발생", e);
            throw new RuntimeException("AI 응답 파싱에 실패했습니다.", e);
        }
    }

    private UtilityAnalysisResult parseUtilityResponse(String aiResponse) {
        try {
            // JSON 부분만 추출 (```json ``` 블록이 있는 경우)
            String jsonContent = aiResponse;
            if (aiResponse.contains("```json")) {
                jsonContent = aiResponse.substring(
                    aiResponse.indexOf("```json") + 7,
                    aiResponse.lastIndexOf("```")
                ).trim();
            }

            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            
            return UtilityAnalysisResult.builder()
                    .amount(new BigDecimal(jsonNode.get("amount").asText()))
                    .date(LocalDateTime.parse(jsonNode.get("date").asText() + "T00:00:00"))
                    .category(jsonNode.get("category").asText())
                    .rawOcrData(aiResponse)
                    .build();

        } catch (Exception e) {
            log.error("공과금 AI 응답 파싱 중 오류 발생", e);
            throw new RuntimeException("AI 응답 파싱에 실패했습니다.", e);
        }
    }

    // 품목 정보 DTO
    public static class ItemInfo {
        private String itemName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal itemAmount;

        // Builder, Getter, Setter
        public static ItemInfoBuilder builder() {
            return new ItemInfoBuilder();
        }

        public static class ItemInfoBuilder {
            private ItemInfo item = new ItemInfo();

            public ItemInfoBuilder itemName(String itemName) {
                item.itemName = itemName;
                return this;
            }

            public ItemInfoBuilder unitPrice(BigDecimal unitPrice) {
                item.unitPrice = unitPrice;
                return this;
            }

            public ItemInfoBuilder quantity(Integer quantity) {
                item.quantity = quantity;
                return this;
            }

            public ItemInfoBuilder itemAmount(BigDecimal itemAmount) {
                item.itemAmount = itemAmount;
                return this;
            }

            public ItemInfo build() {
                return item;
            }
        }

        // Getters
        public String getItemName() { return itemName; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public Integer getQuantity() { return quantity; }
        public BigDecimal getItemAmount() { return itemAmount; }
    }

    // 영수증 분석 결과 DTO
    public static class ReceiptAnalysisResult {
        private BigDecimal amount;
        private LocalDateTime date;
        private String category;
        private String rawOcrData;
        
        // 품목 목록
        private List<ItemInfo> items;

        // Builder, Getter, Setter
        public static ReceiptAnalysisResultBuilder builder() {
            return new ReceiptAnalysisResultBuilder();
        }

        public static class ReceiptAnalysisResultBuilder {
            private ReceiptAnalysisResult result = new ReceiptAnalysisResult();



            public ReceiptAnalysisResultBuilder amount(BigDecimal amount) {
                result.amount = amount;
                return this;
            }

            public ReceiptAnalysisResultBuilder date(LocalDateTime date) {
                result.date = date;
                return this;
            }

            public ReceiptAnalysisResultBuilder category(String category) {
                result.category = category;
                return this;
            }

            public ReceiptAnalysisResultBuilder rawOcrData(String rawOcrData) {
                result.rawOcrData = rawOcrData;
                return this;
            }

            public ReceiptAnalysisResultBuilder items(List<ItemInfo> items) {
                result.items = items;
                return this;
            }

            public ReceiptAnalysisResult build() {
                return result;
            }
        }

        // Getters
        public BigDecimal getAmount() { return amount; }
        public LocalDateTime getDate() { return date; }
        public String getCategory() { return category; }
        public String getRawOcrData() { return rawOcrData; }
        public List<ItemInfo> getItems() { return items; }
    }

    // 공과금 분석 결과 DTO
    public static class UtilityAnalysisResult {
        private BigDecimal amount;
        private LocalDateTime date;
        private String category;
        private String rawOcrData;

        // Builder, Getter, Setter
        public static UtilityAnalysisResultBuilder builder() {
            return new UtilityAnalysisResultBuilder();
        }

        public static class UtilityAnalysisResultBuilder {
            private UtilityAnalysisResult result = new UtilityAnalysisResult();

            public UtilityAnalysisResultBuilder amount(BigDecimal amount) {
                result.amount = amount;
                return this;
            }

            public UtilityAnalysisResultBuilder date(LocalDateTime date) {
                result.date = date;
                return this;
            }

            public UtilityAnalysisResultBuilder category(String category) {
                result.category = category;
                return this;
            }

            public UtilityAnalysisResultBuilder rawOcrData(String rawOcrData) {
                result.rawOcrData = rawOcrData;
                return this;
            }

            public UtilityAnalysisResult build() {
                return result;
            }
        }

        // Getters
        public BigDecimal getAmount() { return amount; }
        public LocalDateTime getDate() { return date; }
        public String getCategory() { return category; }
        public String getRawOcrData() { return rawOcrData; }
    }
}
