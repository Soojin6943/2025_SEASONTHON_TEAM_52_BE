package com.roommate.roommate.matching.repository;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.domain.MyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyProfileRepository extends JpaRepository<MyProfile, Long> {
    Optional<MyProfile> findByUser(User user);

    boolean existsByUser(User user);
}
