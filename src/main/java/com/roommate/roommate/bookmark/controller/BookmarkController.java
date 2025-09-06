//package com.roommate.roommate.bookmark.controller;
//
//import com.roommate.roommate.bookmark.dto.BookmarkRequestDto;
//import com.roommate.roommate.bookmark.dto.BookmarkResponseDto;
//import com.roommate.roommate.bookmark.service.BookmarkService;
//import com.roommate.roommate.common.SuccessResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/bookmarks")
//@RequiredArgsConstructor
//public class BookmarkController {
//
//    private final BookmarkService bookmarkService;
//
//    @PostMapping
//    public ResponseEntity<SuccessResponse<BookmarkResponseDto>> setBookmark(
//            HttpSession session,
//            BookmarkRequestDto requestDto
//    ){
////        Long userId = (Long)session.getAttribute("userId");
//        BookmarkResponseDto dto = bookmarkService.setBookmark(uerId, requestDto);
//        return SuccessResponse.onSuccess("모집글을 성공적으로 북마크했습니다.", HttpStatus.CREATED, dto);
//    }
//}
