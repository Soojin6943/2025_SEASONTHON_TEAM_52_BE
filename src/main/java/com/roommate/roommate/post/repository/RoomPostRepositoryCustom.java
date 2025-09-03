package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.dto.RoomListDto;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomPost;

import java.util.List;

public interface RoomPostRepositoryCustom {
    List<RoomPost> filterPosts(Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, List<HouseType> houseTypes, MoveInDate moveInDate, Integer minStayPeriod);
}
