package com.roommate.roommate.post.dto;

import com.roommate.roommate.auth.domain.Gender;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoommateListDto {
    private Long roommatePostId;
    private Long userId;
    private String username;
    private String userProfile;
    private Integer age;
    private String title;
    private Integer deposit;
    private Integer monthlyRent;
}