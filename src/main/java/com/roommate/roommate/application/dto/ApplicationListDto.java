package com.roommate.roommate.application.dto;

import com.roommate.roommate.application.entity.Status;
import com.roommate.roommate.auth.domain.Gender;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationListDto {
    private Long applicationId;
    private Long roommatePostId;
    private Long roomPostId;
    private Long userId;
    private String userProfile;
    private String username;
    private Integer age;
    private Gender gender;
    private Status status;
}
