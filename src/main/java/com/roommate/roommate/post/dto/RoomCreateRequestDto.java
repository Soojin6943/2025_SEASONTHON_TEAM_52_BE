package com.roommate.roommate.post.dto;

import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RoomCreateRequestDto {
    private String title;
    private Double  latitude;
    private Double longitude;
    private Integer deposit;
    private Integer monthlyRent;
    private Integer managementFee;
    private HouseType houseType;
    private Double size;
    private MoveInDate moveInDate;
    private Integer minStayPeriod;
    private String content;
    private String area;
}
