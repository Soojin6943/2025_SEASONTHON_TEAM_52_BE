package com.roommate.roommate.matching;

import com.roommate.roommate.auth.AuthService;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.dto.DesiredProfileDto;
import com.roommate.roommate.matching.repository.DesiredProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DesiredProfileService {
    private final UserRepository userRepository;
    private final DesiredProfileRepository desiredProfileRepository;


    // 이상형 프로필 생성
    public DesiredProfile create(Long userId, DesiredProfileDto dto){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다"));

        // 이미 존재하는 경우 제외
        if(desiredProfileRepository.findByUserId(userId).isPresent()){
            throw new RuntimeException("이미 이상형 프로필이 존재합니다.");
        }

        DesiredProfile desiredProfile = new DesiredProfile();
        desiredProfile.setUser(user);
        desiredProfile.updateDesiredProfile(dto);

        return desiredProfileRepository.save(desiredProfile);
    }
}
