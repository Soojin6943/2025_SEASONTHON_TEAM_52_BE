package com.roommate.roommate.matching;

import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.domain.MyProfile;
import com.roommate.roommate.matching.dto.ProfileDto;
import com.roommate.roommate.matching.repository.MyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final MyProfileRepository myProfileRepository;

    // 프로필 생성 및 수정
    private void createProfile(Long userId, ProfileDto profileDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다"));

        // 프로필이 존재하는지 확인 후 없으면 생성
        MyProfile myProfile = myProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    MyProfile newMyProfile = new MyProfile();
                    newMyProfile.setUser(user);
                    return newMyProfile;
                });

        // 프로필 생성
        myProfile.updateMyProfile(profileDto);

        // 저장
        myProfileRepository.save(myProfile);
    }
    // 프로필 조회
    // 프로필 수정


    // 처음 가입 시 firstUpdateDto 생성해서 컨트롤러에서 분배해주기 프로필 생성 + 사용자 자기소개, 엠비티아이 추가 등
}
