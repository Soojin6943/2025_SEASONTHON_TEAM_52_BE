package com.roommate.roommate.post.dto;

import com.roommate.roommate.matching.domain.enums.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchedOptionsDto {
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
