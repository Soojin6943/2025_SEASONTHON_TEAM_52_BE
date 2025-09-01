package com.roommate.roommate.matching;

import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.MyProfile;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {

    // 전체 계산식
    public double explainMatch(DesiredProfile desired, MyProfile target) {
        double score = 0.0;
        int totalFactors = 0;

        // 필수 조건 확인 -------------------------
        if (desired.isLifeCycleRequired() && !desired.getLifeCycleValue().equals(target.getLifeCycle())){
            return 0.0;
        }
        if (desired.isSmokingRequired() && !desired.getSmokingValue().equals(target.getSmoking())){
            return 0.0;
        }
        if (desired.isCleanFreqRequired() && !desired.getCleanFreqValue().equals(target.getCleanFreq())){
            return 0.0;
        }
        if (desired.isTidyLevelRequired() && !desired.getTidyLevelValue().equals(target.getTidyLevel())){
            return 0.0;
        }
        if (desired.isVisitorPolicyRequired() && !desired.getVisitorPolicyValue().equals(target.getVisitorPolicy())) {
            return 0.0;
        }
        if (desired.isRestroomUsagePatternRequired() && !desired.getRestroomUsagePatternValue().equals(target.getRestroomUsagePattern())) {
            return 0.0;
        }
        if (desired.isFoodOdorPolicyRequired() && !desired.getFoodOdorPolicyValue().equals(target.getFoodOdorPolicy())) {
            return 0.0;
        }
        if (desired.isHomStayRequired() && !desired.getHomeStayValue().equals(target.getHomeStay())) {
            return 0.0;
        }
        if (desired.isNoisePreferenceRequired() && !desired.getNoisePreferenceValue().equals(target.getNoisePreference())) {
            return 0.0;
        }
        if (desired.isSleepSensitivityRequired() && !desired.getSleepSensitivityValue().equals(target.getSleepSensitivity())) {
            return 0.0;
        }

        // 필수 조건 통과 후 항목 별 점수 계산 ----------------------

        score += calcScore(desired.getLifeCycleValue(), target.getLifeCycle()); totalFactors++;
        score += calcScore(desired.getSmokingValue(), target.getSmoking()); totalFactors++;
        score += calcScore(desired.getCleanFreqValue(), target.getCleanFreq()); totalFactors++;
        score += calcScore(desired.getTidyLevelValue(), target.getTidyLevel()); totalFactors++;
        score += calcScore(desired.getVisitorPolicyValue(), target.getVisitorPolicy()); totalFactors++;
        score += calcScore(desired.getRestroomUsagePatternValue(), target.getRestroomUsagePattern()); totalFactors++;
        score += calcScore(desired.getFoodOdorPolicyValue(), target.getFoodOdorPolicy()); totalFactors++;
        score += calcScore(desired.getHomeStayValue(), target.getHomeStay()); totalFactors++;
        score += calcScore(desired.getNoisePreferenceValue(), target.getNoisePreference()); totalFactors++;
        score += calcScore(desired.getSleepSensitivityValue(), target.getSleepSensitivity()); totalFactors++;

        return (score / totalFactors) * 100.0;
    }

    private double calcScore(Enum<?> desired, Enum<?> actual) {
        if (desired == null) {
            return 1.0;
        }
        return desired.equals(actual) ? 1.0 : 0.0;
    }
}
