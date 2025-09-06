package com.roommate.roommate.bookmark.repository;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.bookmark.entity.Bookmark;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Bookmark findByUser_IdAndRoommatePost(Long userId, RoommatePost roommatePost);

    Bookmark findByUser_IdAndRoomPost(Long userId, RoomPost roomPost);

    List<Bookmark> findAllByUserAndIsBookmarkedOrderByBookmarkIdDesc(User user, Boolean isBookmarked);
}
