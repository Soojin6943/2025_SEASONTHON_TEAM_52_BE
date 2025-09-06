package com.roommate.roommate.bookmark.entity;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = true)
    private RoommatePost roommatePost;

    @ManyToOne
    @JoinColumn(nullable = true)
    private RoomPost roomPost;

    @Column(nullable = false)
    private Boolean isBookmarked;
}
