package com.roommate.roommate.settlement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {

    @Value("${clova.ocr.receipt.url}")
    private String receiptOcrUrl;

    @Value("${clova.ocr.utility.url}")
    private String utilityOcrUrl;

    @Value("${clova.ocr.receipt.secret}")
    private String receiptApiSecret;

    @Value("${clova.ocr.utility.secret}")
    private String utilityApiSecret;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * 영수증 OCR 분석 (Receipt Custom OCR 사용)
     */
    public String analyzeReceipt(byte[] imageBytes) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("version", "V2");
            requestBody.put("requestId", java.util.UUID.randomUUID().toString());
            requestBody.put("timestamp", System.currentTimeMillis());
            requestBody.put("images", new Object[]{
                Map.of("format", "jpg", "name", "receipt", "data", base64Image)
            });

            String response = webClient.post()
                    .uri(receiptOcrUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-OCR-SECRET", receiptApiSecret)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Receipt OCR response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Receipt OCR 분석 중 오류 발생", e);
            throw new RuntimeException("영수증 OCR 분석에 실패했습니다.", e);
        }
    }

    /**
     * 공과금 OCR 분석 (Utility Custom OCR 사용)
     */
    public String analyzeUtility(byte[] imageBytes) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("version", "V2");
            requestBody.put("requestId", java.util.UUID.randomUUID().toString());
            requestBody.put("timestamp", System.currentTimeMillis());
            requestBody.put("images", new Object[]{
                Map.of("format", "jpg", "name", "utility", "data", base64Image)
            });

            String response = webClient.post()
                    .uri(utilityOcrUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-OCR-SECRET", utilityApiSecret)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Utility OCR response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Utility OCR 분석 중 오류 발생", e);
            throw new RuntimeException("공과금 OCR 분석에 실패했습니다.", e);
        }
    }
}
