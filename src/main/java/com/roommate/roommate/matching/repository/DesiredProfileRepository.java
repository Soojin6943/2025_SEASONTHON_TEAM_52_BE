package com.roommate.roommate.matching.repository;

import com.roommate.roommate.matching.domain.DesiredProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesiredProfileRepository extends JpaRepository<DesiredProfile, Long> {
}
