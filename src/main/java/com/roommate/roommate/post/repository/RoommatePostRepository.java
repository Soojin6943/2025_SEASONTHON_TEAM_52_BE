package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoommatePostRepository extends JpaRepository<RoommatePost, Long>, RoommatePostRepositoryCustom {

    boolean existsByUser_IdAndIsRecruitingTrue(Long userId);

    Optional<RoommatePost> findByUser_IdAndIsRecruitingTrue(Long userId);
}
