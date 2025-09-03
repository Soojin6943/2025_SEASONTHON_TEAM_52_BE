package com.roommate.roommate.application.repository;

import com.roommate.roommate.application.entity.Application;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser_IdOrderByApplicationIdDesc(Long userId);

    List<Application> findByRoommatePost(RoommatePost roommatePost);

    List<Application> findByRoomPost(RoomPost roomPost);
}
