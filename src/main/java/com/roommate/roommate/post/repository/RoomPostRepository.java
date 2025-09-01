package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.entity.RoomPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomPostRepository extends JpaRepository<RoomPost, Long> {

    boolean existsByUser_IdAndIsRecruitingTrue(Long userId);
}
