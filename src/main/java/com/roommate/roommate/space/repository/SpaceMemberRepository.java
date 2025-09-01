package com.roommate.roommate.space.repository;

import com.roommate.roommate.space.entity.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceMemberRepository extends JpaRepository<SpaceMember, Long> {
    List<SpaceMember> findBySpaceId(Long spaceId);
    List<SpaceMember> findByUserId(Long userId);
    
    @Query("SELECT sm FROM SpaceMember sm JOIN FETCH sm.space WHERE sm.userId = :userId")
    List<SpaceMember> findByUserIdWithSpace(Long userId);
    
    Optional<SpaceMember> findBySpaceIdAndUserId(Long spaceId, Long userId);
    boolean existsBySpaceIdAndUserId(Long spaceId, Long userId);
    long countBySpaceId(Long spaceId);
    
    // 사용자가 스페이스에 속해있는지 확인
    boolean existsByUserId(Long userId);
    
    // 디버깅을 위한 명시적 쿼리
    @Query("SELECT COUNT(sm) > 0 FROM SpaceMember sm WHERE sm.userId = :userId")
    boolean userExistsInAnySpace(Long userId);
}
