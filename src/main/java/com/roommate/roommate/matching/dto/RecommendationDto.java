package com.roommate.roommate.matching.dto;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.post.dto.MatchedOptionsDto;
import lombok.Getter;

import java.util.List;

@Getter
public class RecommendationDto {
    // 상대방(후보) 유저 정보
    private Long userId;
    private String userName;
    private int userAge;
    // 내가 상대방을 평가한 점수
    private double scoreFromMeToThem;
    // 상대방이 나를 평가한 점수
    private double scoreFromThemToMe;
    // 평균 점수
    private double averageScore;
    // 일치하는 옵션
    private MatchedOptionsDto matchedOptions;

    public RecommendationDto(User user, double scoreAtoB, double scoreBtoA, MatchedOptionsDto matchedOptions){
        this.userId = user.getId();
        this.userName = user.getUsername();
        this.userAge = user.getAge();
        this.matchedOptions = matchedOptions;
        this.scoreFromMeToThem = scoreAtoB;
        this.scoreFromThemToMe = scoreBtoA;
        this.averageScore = (scoreAtoB + scoreBtoA) / 2.0;
    }
}
