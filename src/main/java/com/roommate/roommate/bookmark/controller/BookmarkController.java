package com.roommate.roommate.bookmark.controller;

import com.roommate.roommate.bookmark.dto.BookmarkRequestDto;
import com.roommate.roommate.bookmark.dto.BookmarkResponseDto;
import com.roommate.roommate.bookmark.service.BookmarkService;
import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<SuccessResponse<BookmarkResponseDto>> setBookmark(
            HttpSession session,
            @RequestBody BookmarkRequestDto requestDto
    ){
        Long userId = (Long)session.getAttribute("userId");
        log.info(requestDto.toString());
        log.info(requestDto.getRoommatePostId() + " " + requestDto.getRoomPostId());
        BookmarkResponseDto dto = bookmarkService.setBookmark(userId, requestDto.getRoommatePostId() , requestDto.getRoomPostId());
        return SuccessResponse.onSuccess("모집글을 성공적으로 북마크했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping("/roommate")
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateList>> getRoommateBookmarks(HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        RoommatePostDto.RoommateList dto = bookmarkService.getRoommateBookmarks(userId);
        return SuccessResponse.onSuccess("북마크된 모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping("/room")
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomList>> getRoomBookmarks(HttpSession session){
        Long userId = (Long)session.getAttribute("userId");
        RoomPostDto.RoomList dto = bookmarkService.getRoomBookmarks(userId);
        return SuccessResponse.onSuccess("북마크된 모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }
}
