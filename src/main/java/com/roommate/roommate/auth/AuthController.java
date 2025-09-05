package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.*;
import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.matching.dto.ProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        // 스페이스 소속 여부를 실제 데이터에서 조회하여 업데이트
        authService.updateUserSpaceStatusFromDatabase(user.getId());

        AuthResponse response = new AuthResponse(
                user.getId(), 
                user.getUsername(), 
                user.getAge(), 
                user.getGender(), 
                user.getIntroduction(),
                user.getPreferredLocationEmdCd(),
                user.isHasSpace(),
                created, 
                session.getId()
        );

        return SuccessResponse.onSuccess("로그인에 성공했습니다.",HttpStatus.OK, response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok().build();
    }



    @Operation(summary = "프로필 조회")
    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = authService.findById(userId);
        
        // 스페이스 소속 여부를 실제 데이터에서 조회하여 업데이트
        authService.updateUserSpaceStatusFromDatabase(userId);
        
        UserProfile profile = new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getIntroduction(),
                user.getPreferredLocationEmdCd(),
                user.isHasSpace(),
                user.getKakaoOpenChatLink()
        );
        
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "프로필 업데이트")
    @PutMapping("/profile")
    public ResponseEntity<UserProfile> updateProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = authService.findById(userId);
        
        // 프로필 업데이트
        if (request.introduction() != null) {
            user.updateIntroduction(request.introduction());
        }
        if (request.preferredLocationEmdCd() != null) {
            user.updatePreferredLocation(request.preferredLocationEmdCd());
        }
        if (request.kakaoOpenChatLink() != null) {
            user.updateKakaoOpenChatLink(request.kakaoOpenChatLink());
        }
        
        // 업데이트된 프로필 반환
        UserProfile updatedProfile = new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getAge(),
                user.getGender(),
                user.getIntroduction(),
                user.getPreferredLocationEmdCd(),
                user.isHasSpace(),
                user.getKakaoOpenChatLink()
        );
        
        return ResponseEntity.ok(updatedProfile);
    }

    // 오픈 채팅 링크 추가 : PATCH /users/me/open-chat-link
    @Operation(summary = "오픈 채팅 링크 추가")
    @PatchMapping("/me/open-chat-link")
    public ResponseEntity<SuccessResponse<Void>> setChatLink(@SessionAttribute(name = "userId") Long userId,
                                                             @RequestBody OpenChatLinkRequest requestDto){

        authService.setChatLink(userId, requestDto.getLink());

        // 데이터 없는 응답 반환
        return SuccessResponse.ok("성공적으로 링크를 추가했습니다.");

    }

    @Operation(summary = "사용자 프로필 이미지 등록")
    @PostMapping("/images")
    public ResponseEntity<SuccessResponse<String>> upload(@SessionAttribute("userId") Long userId, @RequestParam("image") MultipartFile imageFile){
        try {
            // 이미지 비었는지 확인
            if (imageFile.isEmpty()) {
                // TODO 예외 처리
            }

            String imageUrl = authService.updateProfileImage(userId, imageFile);

            return SuccessResponse.onSuccess("프로필 이미지를 성공적으로 변경했습니다",
                    HttpStatus.OK,
                    imageUrl
            );
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }

    @Operation(summary = "사용자 성향 수정")
    @PatchMapping("/my-profile")
    public ResponseEntity<SuccessResponse<Void>> updateMyProfile(@SessionAttribute("userId") Long userId, @RequestBody ProfileDto profileDto){

        authService.updateMyProfile(userId, profileDto);

        return SuccessResponse.ok("성공적으로 사용자 성향을 수정했습니다.");
    }

    @Operation(summary = "유저 프로필 조회")
    @GetMapping("/profiles/{userId}")
    public ResponseEntity<SuccessResponse<DetailProfileDto>> getProfile(
            @PathVariable Long userId
    ) {
        DetailProfileDto detailProfileDto = authService.getUserProfile(userId);

        return SuccessResponse.onSuccess("프로필 조회에 성공했습니다.", HttpStatus.OK, detailProfileDto);
    }

}
