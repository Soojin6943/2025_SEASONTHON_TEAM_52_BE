package com.roommate.roommate.matching.dto;

import com.roommate.roommate.auth.domain.User;
import lombok.Getter;

@Getter
public class RecommendationDto {
    // 상대방(후보) 유저 정보
    private User recommendedUser;
    // 내가 상대방을 평가한 점수
    private double scoreFromMeToThem;
    // 상대방이 나를 평가한 점수
    private double scoreFromThemToMe;
    // 평균 점수
    private double averageScore;

    public RecommendationDto(User user, double scoreAtoB, double scoreBtoA){
        this.recommendedUser = user;
        this.scoreFromMeToThem = scoreAtoB;
        this.scoreFromThemToMe = scoreBtoA;
        this.averageScore = (scoreAtoB + scoreBtoA) / 2.0;
    }
}
