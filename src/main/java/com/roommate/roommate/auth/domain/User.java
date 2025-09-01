package com.roommate.roommate.auth.domain;

import com.roommate.roommate.Matching.domain.DesiredProfile;
import com.roommate.roommate.Matching.domain.MyProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "uk_users_username", columnList = "username", unique = true)
)
@Getter
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

    // 생성일
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 내 성향 프로필
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private MyProfile myProfile;

    // 원하는 룸메이트 성향 프로필
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private DesiredProfile desiredProfile;

    public User(String userName, int age, Gender gender, boolean isActive){
        this.username = userName;
        this.age = age;
        this.gender = gender;
        this.isActive = isActive;
    }

    // 오픈 채팅 링크 수정
    public void updateKakaoOpenChatLink(String link) {
        this.kakaoOpenChatLink = link;
    }
}
