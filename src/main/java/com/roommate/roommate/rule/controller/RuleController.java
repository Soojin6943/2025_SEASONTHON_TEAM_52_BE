package com.roommate.roommate.rule.controller;

import com.roommate.roommate.rule.dto.RuleCreateRequest;
import com.roommate.roommate.rule.dto.RuleResponse;
import com.roommate.roommate.rule.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces/{spaceId}/rules")
@RequiredArgsConstructor
@Tag(name = "규칙 관리", description = "반복 규칙 생성, 수정, 삭제, 조회 API")
public class RuleController {
    
    private final RuleService ruleService;
    
    @PostMapping
    @Operation(summary = "규칙 생성", description = "새로운 반복 규칙을 생성합니다.")
    public ResponseEntity<RuleResponse> createRule(
            @PathVariable Long spaceId,
            @RequestBody RuleCreateRequest request) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        
        RuleResponse response = ruleService.createRule(spaceId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{ruleId}")
    @Operation(summary = "규칙 삭제", description = "규칙을 완전히 삭제합니다.")
    public ResponseEntity<Void> deleteRule(
            @PathVariable Long spaceId,
            @PathVariable Long ruleId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (ruleId == null || ruleId <= 0) {
            throw new RuntimeException("유효하지 않은 규칙 ID입니다.");
        }
        ruleService.deleteRule(spaceId, ruleId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{ruleId}")
    @Operation(summary = "규칙 조회", description = "특정 규칙의 상세 정보를 조회합니다.")
    public ResponseEntity<RuleResponse> getRule(
            @PathVariable Long spaceId,
            @PathVariable Long ruleId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (ruleId == null || ruleId <= 0) {
            throw new RuntimeException("유효하지 않은 규칙 ID입니다.");
        }
        RuleResponse response = ruleService.getRule(spaceId, ruleId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "스페이스 규칙 조회", description = "특정 스페이스의 모든 활성 규칙을 조회합니다.")
    public ResponseEntity<List<RuleResponse>> getRulesBySpace(@PathVariable Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        List<RuleResponse> responses = ruleService.getRulesBySpace(spaceId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/today")
    @Operation(summary = "오늘 규칙 조회", description = "오늘 실행되는 규칙만 조회합니다. (메인화면용)")
    public ResponseEntity<List<RuleResponse>> getTodayRules(@PathVariable Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        List<RuleResponse> responses = ruleService.getTodayRules(spaceId);
        return ResponseEntity.ok(responses);
    }
    

}
