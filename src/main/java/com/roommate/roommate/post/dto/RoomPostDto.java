package com.roommate.roommate.post.dto;

import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class RoomPostDto {

    @Getter
    @Setter
    public static class RoomCreateResponseDto {
        private Long roomPostId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomResponseDto {
        private Long roomPostId;
        private Long userId;
        private String username;
        private String title;
        private Double latitude;
        private Double longitude;
        private Integer deposit;
        private Integer monthlyRent;
        private Integer managementFee;
        private HouseType houseType;
        private Double size;
        private MoveInDate moveInDate;
        private Integer minStayPeriod;
        private String content;
        private String photo;
        private String area;
        private LocalDate date;
    }
}
