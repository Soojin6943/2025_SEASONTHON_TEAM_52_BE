package com.roommate.roommate.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RoomPostDto {

    @Getter
    @Setter
    public static class RoomCreateResponseDto {
        private Long roomPostId;
    }
}
