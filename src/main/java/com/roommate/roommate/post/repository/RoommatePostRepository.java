package com.roommate.roommate.post.repository;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.post.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoommatePostRepository extends JpaRepository<RoommatePost, Long>, RoommatePostRepositoryCustom {

    boolean existsByUser_IdAndIsRecruitingTrue(Long userId);

    Optional<RoommatePost> findByUser_IdAndIsRecruitingTrue(Long userId);

    /**
     * [1차 DB 필터링]
     * 1. 모집 공고가 활성화(isRecruiting = true) 상태이고
     * 2. 자기 자신(userId)이 아니면서
     * 3. 지역(area)이 일치하고
     * 4. 성별(gender)이 일치하는
     * 후보자(User) 목록을 조회
     */
    @Query("SELECT p.user FROM RoommatePost p WHERE p.isRecruiting = true AND p.user.id != :userId AND p.area = :location AND p.gender = :gender")
    List<User> findActiveCandidates(
            @Param("userId") Long userId,
            @Param("location") String location,
            @Param("gender") Gender gender
    );
}
