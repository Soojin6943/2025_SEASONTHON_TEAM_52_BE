package com.roommate.roommate.auth.domain;

import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.MyProfile;
import com.roommate.roommate.matching.domain.enums.Mbti;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "uk_users_username", columnList = "username", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // 유저 아이디
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 이름
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    // 유저 나이
    // mvp에서는 나이 랜덤 저장
    @Column(nullable = false)
    private int age;

    // 성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    // 모집 상태
    // 공고 게시물이 활성화 된 상태 (활성화 상태에서만 매칭 후보로)
    private boolean isActive;

    // 카카오 오픈채팅 링크
    @Column
    private String kakaoOpenChatLink;

    // 자기소개
    @Column(columnDefinition = "TEXT")
    private String introduction;

    // 선호지역 (emd_cd)
    @Column(name = "preferred_location_emd_cd", length = 10)
    private String preferredLocationEmdCd;

    // 현재 스페이스 소속 여부
    @Column(name = "has_space", nullable = false)
    private boolean hasSpace = false;

    // mbti
    @Column
    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    // 사용자 프로필 이미지
    @Column
    private String profileImageUrl;

    // 생성일
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 내 성향 프로필
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private MyProfile myProfile;

    // 원하는 룸메이트 성향 프로필
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private DesiredProfile desiredProfile;

    // 오픈 채팅 링크 수정
    public void updateKakaoOpenChatLink(String link) {
        this.kakaoOpenChatLink = link;
    }

    // 자기소개 수정
    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    // 선호지역 수정
    public void updatePreferredLocation(String emdCd) {
        this.preferredLocationEmdCd = emdCd;
    }

    // 스페이스 소속 여부 업데이트
    public void updateHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }

    // mbti 수정
    public void updateMbti(Mbti mbti) {
        this.mbti = mbti;
    }

    // 사용자 프로필 이미지 업로드
    public void updateProfileImage(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
