package com.roommate.roommate.matching;

import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.repository.TestPostRepository;
import com.theokanning.openai.runs.Run;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    // 전체 추첨 흐름 총괄
    /**
     *  --- 성향 기반 매커니즘 ---
     *  내 정보 조회 -> 1차 필터링(DB) -> 2차 필터링(필수 조건)
     *  -> 점수 계산 -> 내림차순 정렬 -> 최종 매칭 후보 리스트 반환
     */

    private final UserRepository userRepository;
    private final MatchingService matchingService;
    // 가정 테스트 (모집 공고)
    private final TestPostRepository postRepository;

    @Transactional
    public List<RecommendationDto> getRecommendations(Long userId, String location){
        // 1. 내 정보 조회
        User userA = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 1차 필터링(DB) : 공고 활성화 + 지역 + 성별
        List<User> candidates = postRepository.findActiveCandidates(
                userA.getId(),
                location,
                userA.getGender()
        );

        List<RecommendationDto> results = new ArrayList<>();

        for (User userB : candidates){
            // 3. 2차 필터링: 필수조건 미충족 → 탈락 (양방향 체크)
            boolean isBGoodForA = matchingService.checkRequiredOptions(userA.getDesiredProfile(), userB.getMyProfile());
            if (!isBGoodForA) {
                continue; // B가 A의 필수조건을 만족하지 않으면 탈락
            }

            boolean isAGoodForB = matchingService.checkRequiredOptions(userB.getDesiredProfile(), userA.getMyProfile());
            if (!isAGoodForB) {
                continue; // A가 B의 필수조건을 만족하지 않으면 탈락
            }

            // 4. 점수 계산
            double scoreAtoB = matchingService.calculateMatchScore(userA.getDesiredProfile(), userB.getMyProfile());
            double scoreBtoA = matchingService.calculateMatchScore(userB.getDesiredProfile(), userA.getMyProfile());

            // 5. 일치하는 옵션 반환 (양방향 일치하는 옵션만, 무관은 제외)
            List<String> matchesFromA = matchingService.getMatchedOptions(userA.getDesiredProfile(), userB.getMyProfile());
            List<String> matchesFromB = matchingService.getMatchedOptions(userB.getDesiredProfile(), userA.getMyProfile());

            List<String> commonMatches = matchesFromA.stream()
                    .filter(matchesFromB::contains)
                    .toList();

            results.add(new RecommendationDto(userB, scoreAtoB, scoreBtoA, commonMatches));

        }

        // 5. 내림차순 정렬
        return results.stream()
                .sorted(Comparator.comparing(RecommendationDto::getAverageScore).reversed())
                .collect(Collectors.toList());

    }
}
