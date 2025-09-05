package com.roommate.roommate.application.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfirmDto {
    private Long applicationId;
    private Long roommatePostId;
    private Long roomPostId;
}
