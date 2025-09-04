package com.roommate.roommate.post.controller;

import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.dto.RoommateCreateRequestDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.service.RoommatePostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/roommate-posts")
@RequiredArgsConstructor
@Slf4j
public class RoommatePostController {

    private final RoommatePostService roommatePostService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateCreateResponseDto>> createRoommatePost(
            HttpSession session,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "meta", required = true) RoommateCreateRequestDto requestDto) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        Long roommatePostId = roommatePostService.createRoommatePost(photo, requestDto, userId);
        RoommatePostDto.RoommateCreateResponseDto dto = new RoommatePostDto.RoommateCreateResponseDto();
        dto.setRoommatePostId(roommatePostId);
        return SuccessResponse.onSuccess("모집글이 성공적으로 저장되었습니다.", HttpStatus.CREATED, dto);
    }

    @GetMapping("/{roommatePostId}")
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateResponseDto>> getRoommatePost(
            HttpSession session,
            @PathVariable(value = "roommatePostId") Long roommatePostId){
        Long userId = (Long) session.getAttribute("userId");
        RoommatePostDto.RoommateResponseDto dto = roommatePostService.getRoommatePost(userId, roommatePostId);
        return SuccessResponse.onSuccess("모집글을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateList>> getRoommatePosts(
            @RequestParam(required = false) Integer depositMin,
            @RequestParam(required = false) Integer depositMax,
            @RequestParam(required = false) Integer rentMin,
            @RequestParam(required = false) Integer rentMax,
            @RequestParam(required = false) String houseType,
            @RequestParam(required = false) MoveInDate moveInDate,
            @RequestParam(required = false) Integer minStay,
            @RequestParam(required = true) String area) {
        RoommatePostDto.RoommateList dto = roommatePostService.getRoommatePosts(area, depositMin, depositMax, rentMin, rentMax, houseType, moveInDate, minStay);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping("/matching")
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateList>> getMatchingRoommatePosts(
            HttpSession session,
            @RequestParam(required = false) Integer depositMin,
            @RequestParam(required = false) Integer depositMax,
            @RequestParam(required = false) Integer rentMin,
            @RequestParam(required = false) Integer rentMax,
            @RequestParam(required = false) String houseType,
            @RequestParam(required = false) MoveInDate moveInDate,
            @RequestParam(required = false) Integer minStay,
            @RequestParam(required = true) String area
    ) {
        Long userId = (Long) session.getAttribute("userId");
        log.info("GET /matching userId={}, area='{}'", userId, area);  // ← 들어오는지 확인
        RoommatePostDto.RoommateList dto = roommatePostService.getMatchingRoommatePosts(userId, area, depositMin, depositMax, rentMin, rentMax, houseType, moveInDate, minStay);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }
}

