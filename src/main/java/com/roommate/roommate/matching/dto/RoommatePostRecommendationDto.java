package com.roommate.roommate.matching.dto;

import com.roommate.roommate.matching.domain.enums.Mbti;
import com.roommate.roommate.post.dto.MatchedOptionsDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoommatePostRecommendationDto {
    private Long roommatePostId;
    private Long userId;
    private String username;
    private String userProfile;
    private Integer age;
    private Mbti mbti;
    private Double score;
    private MatchedOptionsDto matchedOptions;
    private String title;
    private Integer deposit;
    private Integer monthlyRent;
}
