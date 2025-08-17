package com.roommate.roommate.space.repository;

import com.roommate.roommate.space.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findByInviteCode(String inviteCode);
}
