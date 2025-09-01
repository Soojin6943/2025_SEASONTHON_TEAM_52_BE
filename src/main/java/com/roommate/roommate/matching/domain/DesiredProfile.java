package com.roommate.roommate.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roommate.roommate.matching.enums.*;
import com.roommate.roommate.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class DesiredProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 생활 리듬 ---------------------------

    // 생활리듬 값
    // 생활리듬 상관 없을 경우 값이 없을 수도 있음 = null 값 허용
    // null이면 무관 선택한 것
    @Enumerated(EnumType.STRING)
    @Column()
    private LifeCycle lifeCycleValue;

    // 생활 리듬 필수 적용 여부
    @Column(nullable = false)
    private boolean lifeCycleRequired = false;

    // 흡연 ---------------------------

    // 흡연 값
    // 흡연 모드가 상관 없을 경우 값이 없을 수도 있음 = null 값 허용
    // null이면 무관 선택한 것
    @Enumerated(EnumType.STRING)
    @Column()
    private Smoking smokingValue;

    // 흡연 요소 필수 적용 여부
    @Column(nullable = false)
    private boolean smokingRequired = false;

    // 청소 주기 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private CleanFreq cleanFreqValue;

    @Column(nullable = false)
    private boolean cleanFreqRequired = false;

    // 청결 기준 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private TidyLevel tidyLevelValue;

    @Column(nullable = false)
    private boolean tidyLevelRequired = false;

    // 외부인 방문 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private VisitorPolicy visitorPolicyValue;

    @Column(nullable = false)
    private boolean visitorPolicyRequired = false;

    // 욕실 사용 패턴 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private RestroomUsagePattern restroomUsagePatternValue;

    @Column(nullable = false)
    private boolean restroomUsagePatternRequired = false;

    // 음식 냄새 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private FoodOdorPolicy foodOdorPolicyValue;

    @Column(nullable = false)
    private boolean foodOdorPolicyRequired = false;

    // 집에 머무는 시간 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private HomeStay homeStayValue;

    @Column(nullable = false)
    private boolean homStayRequired = false;

    // 소음 시간 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private NoisePreference noisePreferenceValue;

    @Column(nullable = false)
    private boolean noisePreferenceRequired = false;

    // 수면 ---------------------------

    @Enumerated(EnumType.STRING)
    @Column()
    private SleepSensitivity sleepSensitivityValue;

    @Column(nullable = false)
    private boolean sleepSensitivityRequired = false;

    // 최소 나이
    @Column(nullable = false)
    private int minAge;

    // 최대 나이
    @Column(nullable = false)
    private int maxAge;

    public DesiredProfile(User user) {
        this.user = user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
