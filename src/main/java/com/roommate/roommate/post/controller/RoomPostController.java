package com.roommate.roommate.post.controller;

import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.post.dto.RoomCreateRequestDto;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.dto.RoommateCreateRequestDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.service.RoomPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/room-posts")
@RequiredArgsConstructor
public class RoomPostController {

    private final RoomPostService roomPostService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomCreateResponseDto>> createRoomPost(
            HttpSession session,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "meta", required = true) RoomCreateRequestDto requestDto) {
        Long userId = (Long) session.getAttribute("userId");
        Long roomPostId = roomPostService.createRoomPost(photo, requestDto, userId);
        RoomPostDto.RoomCreateResponseDto dto = new RoomPostDto.RoomCreateResponseDto();
        dto.setRoomPostId(roomPostId);
        return SuccessResponse.onSuccess("모집글이 성공적으로 저장되었습니다.", HttpStatus.CREATED, dto);
    }

    @GetMapping("/{roomPostId}")
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomResponseDto>> getRoomPost(
            HttpSession session,
            @PathVariable Long roomPostId) {
        Long userId = (Long) session.getAttribute("userId");
        RoomPostDto.RoomResponseDto dto = roomPostService.getRoomPost(userId, roomPostId);
        return SuccessResponse.onSuccess("모집글을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomList>> getRoomPosts(
            @RequestParam(required = false) Integer depositMin,
            @RequestParam(required = false) Integer depositMax,
            @RequestParam(required = false) Integer rentMin,
            @RequestParam(required = false) Integer rentMax,
            @RequestParam(required = false) String houseType,
            @RequestParam(required = false) MoveInDate moveInDate,
            @RequestParam(required = false) Integer minStay,
            @RequestParam(required = true) String area
    ) {
        RoomPostDto.RoomList dto = roomPostService.getRoomPosts(area, depositMin, depositMax, rentMin, rentMax, houseType, moveInDate, minStay);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping("/matching")
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomList>> getMatchingRoomPosts(
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
        RoomPostDto.RoomList dto = roomPostService.getMatchingRoomPosts(userId, area, depositMin, depositMax, rentMin, rentMax, houseType, moveInDate, minStay);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }
}
