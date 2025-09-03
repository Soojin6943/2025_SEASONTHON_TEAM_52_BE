package com.roommate.roommate.post.repository;

import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoommatePost;

import java.util.List;

public interface RoommatePostRepositoryCustom {
    List<RoommatePost> filterPosts(Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, List<HouseType> houseTypes, MoveInDate moveInDate, Integer minStayPeriod);
}
