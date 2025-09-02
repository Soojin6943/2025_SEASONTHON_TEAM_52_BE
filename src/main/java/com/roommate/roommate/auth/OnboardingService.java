package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.FirstUpdateDto;
import com.roommate.roommate.matching.domain.MyProfile;
import com.roommate.roommate.matching.repository.MyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingService {
    private final UserRepository userRepository;
    private final MyProfileRepository myProfileRepository;

    @Transactional
    public void processFirstUpdate(Long userId, FirstUpdateDto request) {
        // 유저 정보 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        if (myProfileRepository.existsByUser(user)) {
            return;
        }

        MyProfile myProfile = new MyProfile();
        myProfile.setUser(user);
        myProfile.updateMyProfile(request.getProfileDto());
        myProfileRepository.save(myProfile);

        // 자기소개 추가
        user.updateIntroduction(request.getIntroduction());
        // mbti 추가
        user.updateMbti(request.getMbti());
        // 선호 지역 추가
        user.updatePreferredLocation(request.getPreferredLocationEmdCd());

    }
}
