package com.roommate.roommate.post.dto;

import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import lombok.Getter;

@Getter
public class RoommateCreateRequestDto {

    private String title;
    private Double latitude;
    private Double  longitude;
    private Integer deposit;
    private Integer monthlyRent;
    private HouseType houseType;
    private MoveInDate moveInDate;
    private Integer minStayPeriod;
    private String content;
    private String area;
}
