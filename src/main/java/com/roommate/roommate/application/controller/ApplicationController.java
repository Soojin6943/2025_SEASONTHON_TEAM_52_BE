package com.roommate.roommate.application.controller;

import com.roommate.roommate.application.dto.*;
import com.roommate.roommate.application.service.ApplicationService;
import com.roommate.roommate.common.SuccessResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ApplicationResponseDto>> applyPost(
            HttpSession session,
            @RequestBody ApplicationRequestDto requestDto) {
        Long userId = (Long) session.getAttribute("userId");
        Long applicationId = applicationService.applyPost(userId, requestDto.getRoommatePostId(), requestDto.getRoomPostId());
        ApplicationResponseDto responseDto = new ApplicationResponseDto();
        responseDto.setApplicationId(applicationId);
        return SuccessResponse.onSuccess("모집글에 성공적으로 신청했습니다.", HttpStatus.OK, responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<ApplicationList>> getMyApplication(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        ApplicationList dto = new ApplicationList();
        List<ApplicationListDto> response = applicationService.getMyApplication(userId);
        dto.setApplications(response);
        return SuccessResponse.onSuccess("신청글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @PostMapping("/{applicationId}/cancel")
    public ResponseEntity<SuccessResponse<Void>> cancelApplication(
            HttpSession session,
            @PathVariable(value = "applicationId") Long applicationId) {
        Long userId = (Long) session.getAttribute("userId");
        applicationService.cancelApplication(applicationId, userId);
        return SuccessResponse.ok("신청내역을 성공적으로 취소하였습니다.");
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<ApplicationList>> getPostApplication(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        ApplicationList dto = new ApplicationList();
        List<ApplicationListDto> response = applicationService.getPostApplication(userId);
        dto.setApplications(response);
        return SuccessResponse.onSuccess("모집글의 신청내역을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @PostMapping("/{applicationId}/accept")
    public ResponseEntity<SuccessResponse<Void>> acceptApplication(
            HttpSession session,
            @PathVariable(value = "applicationId") Long applicationId) {
        Long userId = (Long) session.getAttribute("userId");
        applicationService.acceptApplication(applicationId, userId);
        return SuccessResponse.ok("신청글을 수락했습니다.");
    }

    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<SuccessResponse<Void>> rejectApplication(
            HttpSession session,
            @PathVariable(value = "applicationId") Long applicationId
    ) {
        Long userId = (Long) session.getAttribute("userId");
        applicationService.rejectApplication(applicationId, userId);
        return SuccessResponse.ok("신청글을 거절했습니다.");
    }

    @PostMapping("/{applicationId}/confirm")
    public ResponseEntity<SuccessResponse<ApplicationConfirmDto>> confirmApplication(
            HttpSession session,
            @PathVariable(value = "applicationId") Long applicationId
    ){
        Long userId = (Long) session.getAttribute("userId");
        ApplicationConfirmDto response = applicationService.confirmApplication(applicationId, userId);
        return SuccessResponse.onSuccess("룸메이트를 확정했습니다.", HttpStatus.OK, response);
    }

    @GetMapping("/{applicationId}/chat")
    public ResponseEntity<SuccessResponse<String>> checkKakao(
            HttpSession session,
            @PathVariable(value = "applicationId") Long applicationId
    ){
        Long userId = (Long) session.getAttribute("userId");
        String kakaoLink = applicationService.getKakaoLink(userId, applicationId);
        return SuccessResponse.onSuccess("상대 유저의 오픈채팅을 가져왔습니다.", HttpStatus.OK, kakaoLink);
    }
}
