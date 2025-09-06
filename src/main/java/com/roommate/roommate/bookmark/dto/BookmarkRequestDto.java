package com.roommate.roommate.bookmark.dto;

import com.roommate.roommate.bookmark.entity.Bookmark;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class BookmarkRequestDto {
    private Long roommatePostId;
    private Long roomPostId;

    public BookmarkRequestDto(Long roommatePostId, Long roomPostId) {
        this.roommatePostId = roommatePostId;
        this.roomPostId = roomPostId;
    }
}
