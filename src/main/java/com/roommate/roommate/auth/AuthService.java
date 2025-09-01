package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.AuthResponse;
import com.roommate.roommate.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // 로그인 (없으면 회원가입)
    @Transactional
    public User loginOrSignUp(LoginRequest req) {
        return userRepository.findByUsername(req.username())
                .orElseGet(() -> createNewUser(req.username()));
    }

    // 처음 로그인 시 자동 회원가입
    private User createNewUser(String name){

        // 랜덤 나이 값
        Random random = new Random();
        int randomAge = (random.nextInt(15)+20);

        // 랜덤 성별
        Gender randomGender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        // 첫 로그인 한 다음에는 비활성화 (아직 공고를 올리지 않았기 때문)
        boolean isActive = false;

        User newUser = User.builder()
                .username(name)
                .age(randomAge)
                .gender(randomGender)
                .isActive(isActive)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Transactional
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }
}
