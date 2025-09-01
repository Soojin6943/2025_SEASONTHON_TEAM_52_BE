package com.roommate.roommate.rule.service;

import com.roommate.roommate.rule.dto.RuleCreateRequest;
import com.roommate.roommate.rule.dto.RuleResponse;
import com.roommate.roommate.rule.entity.Rule;
import com.roommate.roommate.rule.repository.RuleRepository;
import com.roommate.roommate.space.entity.Space;
import com.roommate.roommate.space.repository.SpaceRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleService {
    
    private final RuleRepository ruleRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    
    // 규칙 생성
    @Transactional
    public RuleResponse createRule(Long spaceId, RuleCreateRequest request, Long userId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        // DTO 자체 검증
        if (!request.isValid()) {
            throw new RuntimeException("필수 입력값이 누락되었거나 유효하지 않습니다.");
        }
        
        // 종료일 검증
        if (!request.isEndDateValid()) {
            throw new RuntimeException("종료일은 시작일 이후여야 합니다.");
        }
        
        // 시작일이 과거인지 검증 (선택사항 - 필요에 따라 주석 처리)
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("시작일은 오늘 이후여야 합니다.");
        }
        
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("스페이스를 찾을 수 없습니다. (ID: " + spaceId + ")"));
        
        Rule rule = Rule.builder()
                .space(space)
                .content(request.getContent().trim())
                .weekdays(request.getWeekdays())
                .weekInterval(request.getWeekInterval())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(userId)
                .build();
        
        Rule savedRule = ruleRepository.save(rule);
        return convertToResponse(savedRule);
    }
    
    // 규칙 삭제 (완전 삭제)
    @Transactional
    public void deleteRule(Long spaceId, Long ruleId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (ruleId == null) {
            throw new RuntimeException("규칙 ID를 입력해주세요.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("규칙을 찾을 수 없습니다. (ID: " + ruleId + ")"));
        
        // 규칙이 해당 스페이스에 속하는지 확인
        if (!rule.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("규칙이 해당 스페이스에 속하지 않습니다. (규칙 ID: " + ruleId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        ruleRepository.delete(rule);
    }
    
    // 스페이스의 모든 규칙 조회
    public List<RuleResponse> getRulesBySpace(Long spaceId) {
        if (spaceId == null) {
            throw new RuntimeException("스페이스 ID를 입력해주세요.");
        }
        
        // 스페이스 존재 여부 먼저 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        List<Rule> rules = ruleRepository.findBySpaceId(spaceId);
        return rules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 특정 규칙 조회
    public RuleResponse getRule(Long spaceId, Long ruleId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        if (ruleId == null) {
            throw new RuntimeException("규칙 ID를 입력해주세요.");
        }
        
        // 스페이스 존재 여부 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("규칙을 찾을 수 없습니다. (ID: " + ruleId + ")"));
        
        // 규칙이 해당 스페이스에 속하는지 확인
        if (!rule.getSpace().getId().equals(spaceId)) {
            throw new RuntimeException("규칙이 해당 스페이스에 속하지 않습니다. (규칙 ID: " + ruleId + ", 스페이스 ID: " + spaceId + ")");
        }
        
        return convertToResponse(rule);
    }
    
    // 오늘 해당하는 규칙 조회 (메인화면용)
    public List<RuleResponse> getTodayRules(Long spaceId) {
        if (spaceId == null) {
            throw new RuntimeException("스페이스 ID를 입력해주세요.");
        }
        
        // 스페이스 존재 여부 먼저 확인
        if (!spaceRepository.existsById(spaceId)) {
            throw new RuntimeException("존재하지 않는 스페이스입니다. (ID: " + spaceId + ")");
        }
        
        LocalDate today = LocalDate.now();
        List<Rule> rules = ruleRepository.findRulesInPeriod(spaceId, today, today);
        
        return rules.stream()
                .filter(rule -> shouldExecuteRule(rule, today))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 특정 날짜에 규칙이 실행되어야 하는지 확인
    private boolean shouldExecuteRule(Rule rule, LocalDate date) {
        // 시작일 이전이거나 종료일 이후면 실행하지 않음
        if (date.isBefore(rule.getStartDate()) || 
            (rule.getEndDate() != null && date.isAfter(rule.getEndDate()))) {
            return false;
        }
        
        // 선택된 요일이 아니면 실행하지 않음
        if (!rule.getWeekdays().contains(date.getDayOfWeek())) {
            return false;
        }
        
        // 주기 계산
        long weeksSinceStart = java.time.temporal.ChronoUnit.WEEKS.between(rule.getStartDate(), date);
        return weeksSinceStart % rule.getWeekInterval() == 0;
    }
    
    // DTO 변환
    private RuleResponse convertToResponse(Rule rule) {
        // User 정보 조회하여 이름 설정
        User user = userRepository.findById(rule.getCreatedBy()).orElse(null);
        
        RuleResponse response = new RuleResponse();
        response.setId(rule.getId());
        response.setSpaceId(rule.getSpace().getId());
        response.setContent(rule.getContent());
        response.setWeekdays(rule.getWeekdays());
        response.setWeekInterval(rule.getWeekInterval());
        response.setStartDate(rule.getStartDate());
        response.setEndDate(rule.getEndDate());
        
        response.setCreatedBy(rule.getCreatedBy());
        response.setCreatedByName(user != null ? user.getUsername() : "알 수 없음");
        
        response.setCreatedAt(rule.getCreatedAt());
        response.setUpdatedAt(rule.getUpdatedAt());
        
        return response;
    }
}
