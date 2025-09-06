package com.roommate.roommate.bookmark.service;

import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.bookmark.dto.BookmarkRequestDto;
import com.roommate.roommate.bookmark.dto.BookmarkResponseDto;
import com.roommate.roommate.bookmark.entity.Bookmark;
import com.roommate.roommate.bookmark.repository.BookmarkRepository;
import com.roommate.roommate.post.dto.RoomListDto;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.dto.RoommateListDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;

    @Transactional
    public BookmarkResponseDto setBookmark(Long userId, Long roommatePostId, Long roomPostId) {
        if (userId == null) throw new IllegalArgumentException("userId must not be null");

        RoommatePost rmp = roommatePostRepository.findById(roommatePostId).orElse(null);
        RoomPost rp = roomPostRepository.findById(roomPostId).orElse(null);

        Long testUserId = rmp != null ? testUserId = rmp.getUser().getId() : rp.getUser().getId();

        if (Objects.equals(testUserId, userId)) {
            throw new RuntimeException("자신의 모집글엔 좋아요를 누를 수 없습니다.");
        }

        Bookmark bookmark = null;

        BookmarkResponseDto result = new BookmarkResponseDto();
        if (rmp != null) {
            bookmark = bookmarkRepository.findByUser_IdAndRoommatePost(userId, rmp);
        } else if (rp != null) {
            bookmark = bookmarkRepository.findByUser_IdAndRoomPost(userId, rp);
        }

        if (bookmark != null) {
            bookmark.setIsBookmarked(!Boolean.TRUE.equals(bookmark.getIsBookmarked()));
            log.info("bookmarkId: " + bookmark.getBookmarkId());
            bookmarkRepository.save(bookmark);
            result.setBookmarkId(bookmark.getBookmarkId());
        }
        else {
            Bookmark bk = new Bookmark();
            bk.setUser(userRepository.findById(userId).orElse(null));
            bk.setRoommatePost(rmp);
            bk.setRoomPost(rp);
            bk.setIsBookmarked(true);
            bookmarkRepository.save(bk);
            result.setBookmarkId(bk.getBookmarkId());
            }

        return result;
    }

    public RoommatePostDto.RoommateList getRoommateBookmarks(Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        List<Bookmark> bookmarks = bookmarkRepository.findAllByUserAndIsBookmarkedOrderByBookmarkIdDesc(user, true);
        List<RoommateListDto> roommatePosts = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            RoommateListDto dto = RoommateListDto.builder()
                    .roommatePostId(bookmark.getRoommatePost().getRoommatePostId())
                    .userId(userId)
                    .username(user.getUsername())
                    .userProfile(user.getProfileImageUrl())
                    .age(user.getAge())
                    .title(bookmark.getRoommatePost().getTitle())
                    .deposit(bookmark.getRoommatePost().getDeposit())
                    .monthlyRent(bookmark.getRoommatePost().getMonthlyRent())
                    .build();
            roommatePosts.add(dto);
        }

        RoommatePostDto.RoommateList dto = new RoommatePostDto.RoommateList();
        dto.setPosts(roommatePosts);
        return dto;
    }

    public RoomPostDto.RoomList getRoomBookmarks(Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        List<Bookmark> bookmarks = bookmarkRepository.findAllByUserAndIsBookmarkedOrderByBookmarkIdDesc(user, true);
        List<RoomListDto> roomPosts = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            RoomListDto dto = RoomListDto.builder()
                    .roomPostId(bookmark.getRoomPost().getRoomPostId())
                    .userId(userId)
                    .username(user.getUsername())
                    .userProfile(user.getProfileImageUrl())
                    .age(user.getAge())
                    .title(bookmark.getRoommatePost().getTitle())
                    .deposit(bookmark.getRoommatePost().getDeposit())
                    .monthlyRent(bookmark.getRoommatePost().getMonthlyRent())
                    .build();
            roomPosts.add(dto);
        }

        RoomPostDto.RoomList dto = new RoomPostDto.RoomList();
        dto.setPosts(roomPosts);
        return dto;
    }

}
