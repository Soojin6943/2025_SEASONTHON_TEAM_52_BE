//package com.roommate.roommate.bookmark.service;
//
//import com.roommate.roommate.auth.UserRepository;
//import com.roommate.roommate.bookmark.dto.BookmarkRequestDto;
//import com.roommate.roommate.bookmark.dto.BookmarkResponseDto;
//import com.roommate.roommate.bookmark.entity.Bookmark;
//import com.roommate.roommate.bookmark.repository.BookmarkRepository;
//import com.roommate.roommate.post.entity.RoomPost;
//import com.roommate.roommate.post.entity.RoommatePost;
//import com.roommate.roommate.post.repository.RoomPostRepository;
//import com.roommate.roommate.post.repository.RoommatePostRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class BookmarkService {
//
//    private final BookmarkRepository bookmarkRepository;
//    private final UserRepository userRepository;
//    private final RoommatePostRepository roommatePostRepository;
//    private final RoomPostRepository roomPostRepository;
//
//    public BookmarkResponseDto setBookmark(Long userId, BookmarkRequestDto requestDto) {
//
//        RoommatePost rmp = roommatePostRepository.findById(requestDto.getRoommatePostId()).orElse(null);
//        RoomPost rp = roomPostRepository.findById(requestDto.getRoomPostId()).orElse(null);
//
//        if (bookmarkRepositry.findByUser_UserIdAnd)
//
//        Bookmark bookmark = new Bookmark();
//        bookmark.setBookmarkId(userId);
//        bookmark.setUser(userRepository.findById(userId).get());
//        bookmark.setRoommatePost(rmp);
//        bookmark.setRoomPost(rp);
//        bookmark.setIsBookmarked(true);
//    }
//
//}
