package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoommatePostRepository extends JpaRepository<RoommatePost, Long> {

    boolean existsByUser_IdAndIsRecruitingTrue(Long userId);
}
