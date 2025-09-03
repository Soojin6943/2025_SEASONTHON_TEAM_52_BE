package com.roommate.roommate.post.dto;

import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class RoommateCreateRequestDto {

    private String title;
    private Double latitude;
    private Double  longitude;
    private Integer deposit;
    private Integer monthlyRent;
    private List<HouseType> houseTypes;
    private MoveInDate moveInDate;
    private Integer minStayPeriod;
    private String content;
    private String area;
}
