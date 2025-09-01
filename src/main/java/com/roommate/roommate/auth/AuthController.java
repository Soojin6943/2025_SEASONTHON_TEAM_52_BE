package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.AuthResponse;
import com.roommate.roommate.auth.dto.LoginRequest;
import com.roommate.roommate.common.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인(없으면 자동 회원가입)")
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpServletRequest) {
        // 서비스에서 유저 정보 조회 (없으면 자동 회원가입)
        User user = authService.loginOrSignUp(request);

        // 세션 가져오기, 없으면 새로 생성
        HttpSession session = httpServletRequest.getSession(true);

        // 세션에 사용자 id, 이름 저장
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());

        // 세션 유지 시간 30분
        session.setMaxInactiveInterval(1800);

        // created 여부 확인 (회원가입 직후 5초 이내면 true)
        boolean created = user.getCreatedAt() != null && user.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5));

        AuthResponse response = new AuthResponse(user.getId(), user.getUsername(), user.getAge(), user.getGender(), created, session.getId());

        return SuccessResponse.onSuccess("로그인에 성공했습니다.",HttpStatus.OK, response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현재 세션 정보 확인")
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        
        if (userId == null || username == null) {
            return ResponseEntity.status(401).build();
        }

        User user = authService.findById(userId);
        
        return ResponseEntity.ok(new AuthResponse(
                userId,
                username,
                user.getAge(),
                user.getGender(),
                false,
                session.getId()
        ));
    }
}
