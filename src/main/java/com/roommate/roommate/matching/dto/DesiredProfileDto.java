package com.roommate.roommate.matching.dto;

import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DesiredProfileDto {
    private LifeCycle lifeCycleValue;
    private boolean lifeCycleRequired;

    private Smoking smokingValue;
    private boolean smokingRequired;

    private CleanFreq cleanFreqValue;
    private boolean cleanFreqRequired;

    private TidyLevel tidyLevelValue;
    private boolean tidyLevelRequired;

    private VisitorPolicy visitorPolicyValue;
    private boolean visitorPolicyRequired;

    private RestroomUsagePattern restroomUsagePatternValue;
    private boolean restroomUsagePatternRequired;

    private FoodOdorPolicy foodOdorPolicyValue;
    private boolean foodOdorPolicyRequired;

    private HomeStay homeStayValue;
    private boolean homeStayRequired;

    private NoisePreference noisePreferenceValue;
    private boolean noisePreferenceRequired;

    private SleepSensitivity sleepSensitivityValue;
    private boolean sleepSensitivityRequired;

    public DesiredProfileDto(DesiredProfile entity) {
        this.lifeCycleValue = entity.getLifeCycleValue();
        this.lifeCycleRequired = entity.isLifeCycleRequired();
        this.smokingValue = entity.getSmokingValue();
        this.smokingRequired = entity.isSmokingRequired();
        this.cleanFreqValue = entity.getCleanFreqValue();
        this.cleanFreqRequired = entity.isCleanFreqRequired();
        this.tidyLevelValue = entity.getTidyLevelValue();
        this.tidyLevelRequired = entity.isTidyLevelRequired();
        this.visitorPolicyValue = entity.getVisitorPolicyValue();
        this.visitorPolicyRequired = entity.isVisitorPolicyRequired();
        this.restroomUsagePatternValue = entity.getRestroomUsagePatternValue();
        this.restroomUsagePatternRequired = entity.isRestroomUsagePatternRequired();
        this.foodOdorPolicyValue = entity.getFoodOdorPolicyValue();
        this.foodOdorPolicyRequired = entity.isFoodOdorPolicyRequired();
        this.homeStayValue = entity.getHomeStayValue();
        this.homeStayRequired = entity.isHomStayRequired(); // 엔티티의 필드명(homStayRequired) 오타에 맞춰 호출
        this.noisePreferenceValue = entity.getNoisePreferenceValue();
        this.noisePreferenceRequired = entity.isNoisePreferenceRequired();
        this.sleepSensitivityValue = entity.getSleepSensitivityValue();
        this.sleepSensitivityRequired = entity.isSleepSensitivityRequired();
    }
}
