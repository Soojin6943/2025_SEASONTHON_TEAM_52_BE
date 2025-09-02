package com.roommate.roommate.matching.dto;

import com.roommate.roommate.matching.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileDto {
    private Long userid;
    private LifeCycle lifeCycle;
    private Smoking smoking;
    private CleanFreq cleanFreq;
    private TidyLevel tidyLevel;
    private VisitorPolicy visitorPolicy;
    private RestroomUsagePattern restroomUsagePattern;
    private FoodOdorPolicy foodOdorPolicy;
    private HomeStay homeStay;
    private NoisePreference noisePreference;
    private SleepSensitivity sleepSensitivity;
}

