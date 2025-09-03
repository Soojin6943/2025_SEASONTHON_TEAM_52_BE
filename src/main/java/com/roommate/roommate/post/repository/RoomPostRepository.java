package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.entity.RoomPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomPostRepository extends JpaRepository<RoomPost, Long>, RoomPostRepositoryCustom{

    boolean existsByUser_IdAndIsRecruitingTrue(Long userId);

    Optional<RoomPost> findByUser_IdAndIsRecruitingTrue(Long userId);


}
