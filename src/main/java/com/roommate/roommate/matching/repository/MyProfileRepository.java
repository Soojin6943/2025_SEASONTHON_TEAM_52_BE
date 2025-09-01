package com.roommate.roommate.matching.repository;

import com.roommate.roommate.matching.domain.MyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyProfileRepository extends JpaRepository<MyProfile, Long> {
}
