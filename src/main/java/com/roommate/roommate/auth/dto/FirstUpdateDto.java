package com.roommate.roommate.auth.dto;

import com.roommate.roommate.matching.domain.enums.Mbti;
import com.roommate.roommate.matching.dto.ProfileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FirstUpdateDto {
    private ProfileDto profileDto;
    private Mbti mbti;
    private String introduction;
    private String preferredLocationEmdCd;
}
