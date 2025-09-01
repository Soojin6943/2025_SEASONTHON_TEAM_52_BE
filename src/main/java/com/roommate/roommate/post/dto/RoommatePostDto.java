package com.roommate.roommate.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RoommatePostDto {

    @Getter
    @Setter
    public static class RoommateCreateResponseDto {
        private Long roommatePostId;
    }
}
