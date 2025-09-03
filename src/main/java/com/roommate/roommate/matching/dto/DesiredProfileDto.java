package com.roommate.roommate.matching.dto;

import com.roommate.roommate.matching.domain.enums.*;
import lombok.Getter;

@Getter
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
}
