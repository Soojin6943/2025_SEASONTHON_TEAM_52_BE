package com.roommate.roommate.post.dto;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.matching.domain.enums.Mbti;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class RoommatePostDto {

    @Getter
    @Setter
    public static class RoommateCreateResponseDto {
        private Long roommatePostId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoommateResponseDto {
        private Long roommatePostId;
        private Long userId;
        private String username;
        private Integer age;
        private Gender gender;
        private Mbti mbti;
        private String title;
        private Double latitude;
        private Double longitude;
        private Integer deposit;
        private Integer monthlyRent;
        private List<HouseType> houseTypes;
        private MoveInDate moveInDate;
        private Integer minStayPeriod;
        private String content;
        private String photo;
        private String area;
        private LocalDate date;
    }

    @Getter
    @Setter
    public static class RoommateList {
        private List<RoommateListDto> posts;
    }
}
