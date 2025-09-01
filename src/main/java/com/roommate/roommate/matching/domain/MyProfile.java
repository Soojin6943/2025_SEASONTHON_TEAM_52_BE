package com.roommate.roommate.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.enums.*;
import jakarta.persistence.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class MyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지연 로딩 LAZY
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // 생활 리듬
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LifeCycle lifeCycle;

    // 흡연
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Smoking smoking;

    // 청소 주기
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CleanFreq cleanFreq;

    // 청결 기준
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TidyLevel tidyLevel;

    // 외부인 방문
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitorPolicy visitorPolicy;

    // 욕실 사용 패턴
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestroomUsagePattern restroomUsagePattern;

    // 음식 냄새
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodOdorPolicy foodOdorPolicy;

    // 집에 머무는 시간
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeStay homeStay;

    // 소음 시간
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoisePreference noisePreference;

    // 수면
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SleepSensitivity sleepSensitivity;

    public void setUser(User user) {this.user = user;}

}
