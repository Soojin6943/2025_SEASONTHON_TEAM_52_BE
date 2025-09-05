package com.roommate.roommate.matching;

import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.dto.RoomPostRecommendationDto;
import com.roommate.roommate.matching.dto.RoommatePostRecommendationDto;
import com.roommate.roommate.matching.repository.TestPostRepository;
import com.roommate.roommate.post.dto.MatchedOptionsDto;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    // 전체 추첨 흐름 총괄
    /**
     *  --- 성향 기반 매커니즘 ---
     *  내 정보 조회 -> 1차 필터링(DB) -> 2차 필터링(필수 조건)
     *  -> 점수 계산 -> 내림차순 정렬 -> 최종 매칭 후보 리스트 반환
     */

    private final UserRepository userRepository;
    private final MatchingService matchingService;
    private final TestPostRepository postRepository;
    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;


    @Transactional
    public List<RecommendationDto> getRecommendations(Long userId, String location, Boolean room){
        // 1. 내 정보 조회
        User userA = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 1차 필터링(DB) : 공고 활성화 + 지역 + 성별
        // room 유무 값에 따라 적절한 Repository를 사용하여 후보자 목록 조회
        List<User> candidates;
        if (room) {
            // room이 true이면 RoomPostRepository 사용
            candidates = roomPostRepository.findActiveCandidates(
                    userA.getId(),
                    location,
                    userA.getGender()
            );
        } else {
            // room이 false이면 RoommatePostRepository 사용
            candidates = roommatePostRepository.findActiveCandidates(
                    userA.getId(),
                    location,
                    userA.getGender()
            );
        }

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
            MatchedOptionsDto matchesFromA = matchingService.getMatchedOptions(userA.getDesiredProfile(), userB.getMyProfile());
            MatchedOptionsDto matchesFromB = matchingService.getMatchedOptions(userB.getDesiredProfile(), userA.getMyProfile());

            MatchedOptionsDto commonMatches = checkMatchedOptions(matchesFromA, matchesFromB);

            results.add(new RecommendationDto(userB, scoreAtoB, scoreBtoA, commonMatches));
        }

        // 5. 내림차순 정렬
        return results.stream()
                .sorted(Comparator.comparing(RecommendationDto::getAverageScore).reversed())
                .collect(Collectors.toList());

    }

    public MatchedOptionsDto checkMatchedOptions(MatchedOptionsDto matchesFromA, MatchedOptionsDto matchesFromB) {
        MatchedOptionsDto commonMatches = MatchedOptionsDto.builder()
                .lifeCycle(matchesFromA.getLifeCycle() == matchesFromB.getLifeCycle() ? matchesFromA.getLifeCycle() : null)
                .smoking(matchesFromA.getSmoking() == matchesFromB.getSmoking() ? matchesFromA.getSmoking() : null)
                .cleanFreq(matchesFromA.getCleanFreq() == matchesFromB.getCleanFreq() ? matchesFromA.getCleanFreq() : null)
                .tidyLevel(matchesFromA.getTidyLevel() == matchesFromB.getTidyLevel() ? matchesFromA.getTidyLevel() : null)
                .visitorPolicy(matchesFromA.getVisitorPolicy() == matchesFromB.getVisitorPolicy() ? matchesFromA.getVisitorPolicy() : null)
                .restroomUsagePattern(matchesFromA.getRestroomUsagePattern() == matchesFromB.getRestroomUsagePattern() ? matchesFromA.getRestroomUsagePattern() : null)
                .foodOdorPolicy(matchesFromA.getFoodOdorPolicy() == matchesFromB.getFoodOdorPolicy() ? matchesFromA.getFoodOdorPolicy() : null)
                .homeStay(matchesFromA.getHomeStay() == matchesFromB.getHomeStay() ? matchesFromA.getHomeStay() : null)
                .noisePreference(matchesFromA.getNoisePreference() == matchesFromB.getNoisePreference() ? matchesFromA.getNoisePreference() : null)
                .sleepSensitivity(matchesFromA.getSleepSensitivity() == matchesFromB.getSleepSensitivity() ? matchesFromA.getSleepSensitivity() : null)
                .build();

        return commonMatches;
    }

    @Transactional(readOnly = true)
    public List<RoommatePostRecommendationDto> getRoommateRecommendations(Long userId, String area){

        List<RecommendationDto> recommendations = getRecommendations(userId, area, false);
        if (recommendations.isEmpty()) return List.of();

        List<RoommatePostRecommendationDto> results = new ArrayList<>();
        for (RecommendationDto recommendation : recommendations){
            Long id =  recommendation.getUserId();
            User user = userRepository.findById(id).orElseThrow();
            RoommatePost rmp = roommatePostRepository.findByUser_IdAndIsRecruitingTrue(id).orElse(null);
            // 그 유저는 넘어가
            if (rmp == null)
                continue;
            RoommatePostRecommendationDto result = RoommatePostRecommendationDto.builder()
                    .roommatePostId(rmp.getRoommatePostId())
                    .userId(id)
                    .username(user.getUsername())
                    .userProfile(user.getProfileImageUrl())
                    .age(user.getAge())
                    .mbti(user.getMbti())
                    .score(recommendation.getAverageScore())
                    .matchedOptions(recommendation.getMatchedOptions())
                    .title(rmp.getTitle())
                    .deposit(rmp.getDeposit() != null ? rmp.getDeposit() : null)
                    .monthlyRent(rmp.getMonthlyRent() != null ? rmp.getMonthlyRent() : null)
                    .build();

            results.add(result);
        }
        return results;
    }

    @Transactional(readOnly = true)
    public List<RoomPostRecommendationDto> getRoomRecommendations(Long userId, String area){

        List<RecommendationDto> recommendations = getRecommendations(userId, area, true);
        if (recommendations.isEmpty()) return List.of();

        List<RoomPostRecommendationDto> results = new ArrayList<>();
        for (RecommendationDto recommendation : recommendations){
            Long id =  recommendation.getUserId();
            User user = userRepository.findById(id).orElseThrow();
            RoomPost rp = roomPostRepository.findByUser_IdAndIsRecruitingTrue(id).orElse(null);
            // 그 유저는 넘어가
            if (rp == null)
                continue;
            RoomPostRecommendationDto result = RoomPostRecommendationDto.builder()
                    .roomPostId(rp.getRoomPostId())
                    .userId(id)
                    .username(user.getUsername())
                    .userProfile(user.getProfileImageUrl())
                    .age(user.getAge())
                    .mbti(user.getMbti())
                    .score(recommendation.getAverageScore())
                    .matchedOptions(recommendation.getMatchedOptions())
                    .title(rp.getTitle())
                    .deposit(rp.getDeposit() != null ? rp.getDeposit() : null)
                    .monthlyRent(rp.getMonthlyRent() != null ? rp.getMonthlyRent() : null)
                    .build();
            results.add(result);
        }
        return results;
    }
}
