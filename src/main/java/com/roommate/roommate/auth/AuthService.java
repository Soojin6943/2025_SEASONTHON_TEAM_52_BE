package com.roommate.roommate.auth;

import com.roommate.roommate.auth.dto.AuthResponse;
import com.roommate.roommate.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public AuthResponse loginOrSignUp(LoginRequest req, HttpSession session) {
        return userRepository.findByUsername(req.username())
                .map(u -> {
                    // 세션에 사용자 정보 저장 (회원가입 X)
                    session.setAttribute("userId", u.getId());
                    session.setAttribute("username", u.getUsername());
                    return new AuthResponse(u.getId(), u.getUsername(), false, session.getId());
                })
                .orElseGet(() -> {
                    // 계정없으면 회원가입
                    User created = User.builder()
                            .username(req.username())
                            .createdAt(LocalDateTime.now())
                            .build();
                    userRepository.save(created);
                    
                    // 세션에 사용자 정보 저장
                    session.setAttribute("userId", created.getId());
                    session.setAttribute("username", created.getUsername());
                    return new AuthResponse(created.getId(), created.getUsername(), true, session.getId());
                });
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
