package com.roommate.roommate.space.repository;

import com.roommate.roommate.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
    List<SpaceMember> findBySpaceId(Long spaceId);
    List<SpaceMember> findByUserId(Long userId);
    Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);
    boolean existsBySpaceIdAndUserId(Long spaceId, Long userId);
    long countBySpaceId(Long spaceId);
}
