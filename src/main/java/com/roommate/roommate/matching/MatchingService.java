package com.roommate.roommate.matching;

import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.MyProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
public class MatchingService {
    /**
     *  --- 성향 기반 매커니즘 ---
     *  내 정보 조회 -> 1차 필터링(DB) -> 2차 필터링(필수 조건)
     *  -> 점수 계산 -> 내림차순 정렬 -> 최종 매칭 후보 리스트 반환
     */

    /**
     * [2차 필터링] 필수 조건들을 모두 만족하는지 검사하는 메서드
     * @return 모든 필수 조건을 만족하면 true, 하나라도 만족하지 않으면 false
     */
    public boolean checkRequiredOptions(DesiredProfile desired, MyProfile target) {
        return Stream.of(
                check(desired.isLifeCycleRequired(), desired.getLifeCycleValue(), target.getLifeCycle()),
                check(desired.isSmokingRequired(), desired.getSmokingValue(), target.getSmoking()),
                check(desired.isCleanFreqRequired(), desired.getCleanFreqValue(), target.getCleanFreq()),
                check(desired.isTidyLevelRequired(), desired.getTidyLevelValue(), target.getTidyLevel()),
                check(desired.isVisitorPolicyRequired(), desired.getVisitorPolicyValue(), target.getVisitorPolicy()),
                check(desired.isRestroomUsagePatternRequired(), desired.getRestroomUsagePatternValue(), target.getRestroomUsagePattern()),
                check(desired.isFoodOdorPolicyRequired(), desired.getFoodOdorPolicyValue(), target.getFoodOdorPolicy()),
                check(desired.isHomStayRequired(), desired.getHomeStayValue(), target.getHomeStay()),
                check(desired.isNoisePreferenceRequired(), desired.getNoisePreferenceValue(), target.getNoisePreference()),
                check(desired.isSleepSensitivityRequired(), desired.getSleepSensitivityValue(), target.getSleepSensitivity())
        ).allMatch(Predicate.isEqual(true)); // 모든 검사를 통과해야 true 반환
    }

    // 필수 조건일 경우, 값이 일치하는지 확인
    private <T> boolean check(boolean isRequired, T desiredValue, T actualValue){
        if (!isRequired){
            return true;
        }
        return desiredValue != null && desiredValue.equals(actualValue);
    }

    /**
     * [점수 계산] 두 프로필 간의 최종 일치 점수를 계산하는 메서드
     * @return 0점에서 100점 사이의 점수
     */
    public double calculateMatchScore(DesiredProfile desired, MyProfile target) {
        double totalScore = Stream.of(
                score(desired.getLifeCycleValue(), target.getLifeCycle()),
                score(desired.getSmokingValue(), target.getSmoking()),
                score(desired.getCleanFreqValue(), target.getCleanFreq()),
                score(desired.getTidyLevelValue(), target.getTidyLevel()),
                score(desired.getVisitorPolicyValue(), target.getVisitorPolicy()),
                score(desired.getRestroomUsagePatternValue(), target.getRestroomUsagePattern()),
                score(desired.getFoodOdorPolicyValue(), target.getFoodOdorPolicy()),
                score(desired.getHomeStayValue(), target.getHomeStay()),
                score(desired.getNoisePreferenceValue(), target.getNoisePreference()),
                score(desired.getSleepSensitivityValue(), target.getSleepSensitivity())
        ).mapToDouble(Double::doubleValue).sum();

        // 총 10개 항목이므로 10으로 나눔
        return (totalScore / 10.0) * 100.0;
    }

    // 각 항목 점수 계산 (무관은 1점, 그 외는 일치해야 1점)
    private <T> double score(T desiredValue, T actualValue) {
        if (desiredValue == null) {
            return 1.0;
        }
        return desiredValue.equals(actualValue) ? 1.0 : 0.0;
    }

    // 일치하는 옵션
    public List<String> getMatchedOptions(DesiredProfile desired, MyProfile target) {
        List<String> matched = new ArrayList<>();

        if (isMatch(desired.getLifeCycleValue(), target.getLifeCycle())) matched.add("life_cycle:" + target.getLifeCycle());
        if (isMatch(desired.getSmokingValue(), target.getSmoking())) matched.add("smoking:" + target.getSmoking());
        if (isMatch(desired.getCleanFreqValue(), target.getCleanFreq())) matched.add("clean_freq:" + target.getCleanFreq());
        if (isMatch(desired.getTidyLevelValue(), target.getTidyLevel())) matched.add("tidy_level:" + target.getTidyLevel());
        if (isMatch(desired.getVisitorPolicyValue(), target.getVisitorPolicy())) matched.add("visitor_policy:" + target.getVisitorPolicy());
        if (isMatch(desired.getRestroomUsagePatternValue(), target.getRestroomUsagePattern())) matched.add("restroom_usage_pattern:" + target.getRestroomUsagePattern());
        if (isMatch(desired.getFoodOdorPolicyValue(), target.getFoodOdorPolicy())) matched.add("food_odor_policy:" + target.getFoodOdorPolicy());
        if (isMatch(desired.getHomeStayValue(), target.getHomeStay())) matched.add("home_stay:" + target.getHomeStay());
        if (isMatch(desired.getNoisePreferenceValue(), target.getNoisePreference())) matched.add("noise_preference:" + target.getNoisePreference());
        if (isMatch(desired.getSleepSensitivityValue(), target.getSleepSensitivity())) matched.add("sleep_sensitivity:" + target.getSleepSensitivity());

        return matched;
    }

    private <T> boolean isMatch(T desiredValue, T actualValue) {
        if (desiredValue == null) return false; // 무관이면 매칭 포인트 X
        return desiredValue.equals(actualValue);
    }
}
