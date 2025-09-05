package com.roommate.roommate.auth;

import com.roommate.roommate.auth.dto.FirstUpdateDto;
import com.roommate.roommate.common.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Onboarding")
@RestController
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @Operation(summary = "첫 로그인 후 온보딩 정보 저장")
    @PostMapping("/onboards")
    public ResponseEntity<SuccessResponse<Void>> firstUpload(@SessionAttribute("userId") Long userId, @RequestBody FirstUpdateDto request){

        onboardingService.processFirstUpdate(userId, request);

        return SuccessResponse.ok("온보딩 저장에 성공했습니다");
    }
}
