package com.roommate.roommate.post.dto;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.matching.domain.enums.Mbti;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomNum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
        private Integer age;
        private Gender gender;
        private Mbti mbti;
        private MatchedOptionsDto matchedOptions;
        private String title;
        private Double latitude;
        private Double longitude;
        private Integer deposit;
        private Integer monthlyRent;
        private Integer managementFee;
        private HouseType houseType;
        private RoomNum roomNum;
        private Double size;
        private MoveInDate moveInDate;
        private Integer minStayPeriod;
        private String content;
        private String photo;
        private String area;
        private LocalDate date;
    }

    @Getter
    @Setter
    public static class RoomList {
        private List<RoomListDto> posts;
    }
}
