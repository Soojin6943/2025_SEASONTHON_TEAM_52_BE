package com.roommate.roommate.auth.dto;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.enums.Mbti;
import com.roommate.roommate.matching.dto.DesiredProfileDto;
import com.roommate.roommate.matching.dto.ProfileDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailProfileDto {
    // 사용자 기본 정보
    private String name;
    private int age;
    private Gender gender;
    private Mbti mbti;

    private ProfileDto myProfile;
    private DesiredProfileDto desiredProfile;

    // TODO 서비스 만들기
}
