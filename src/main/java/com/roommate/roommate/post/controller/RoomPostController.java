package com.roommate.roommate.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.post.dto.RoomCreateRequestDto;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.service.RoomPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/room-posts")
@RequiredArgsConstructor
@Slf4j
public class RoomPostController {

    private final RoomPostService roomPostService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "방 모집글 생성", description = "사진과 함께 방 모집글을 생성합니다. 좌표값으로부터 지역 정보가 자동으로 설정됩니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<RoomPostDto.RoomCreateResponseDto>> createRoomPost(
            HttpSession session,
            @RequestPart(value = "photo", required = false) 
            @Schema(description = "업로드할 사진 파일 (선택사항)") 
            MultipartFile photo,
            @RequestPart(value = "meta", required = true) 
            @Schema(description = "방 모집글 메타데이터 (JSON 문자열)", 
                    example = "{\"title\":\"강남구 원룸 방 구해요\",\"content\":\"강남구 역삼동 원룸에서 룸메이트를 구합니다.\",\"latitude\":37.5665,\"longitude\":126.9780,\"deposit\":1000,\"monthlyRent\":50,\"managementFee\":5,\"houseType\":\"APARTMENT\",\"size\":25.5,\"moveInDate\":\"Q1\",\"minStayPeriod\":6}") 
            String metaJson) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            
            RoomCreateRequestDto requestDto = objectMapper.readValue(metaJson, RoomCreateRequestDto.class);
            
            Long roomPostId = roomPostService.createRoomPost(photo, requestDto, userId);
            RoomPostDto.RoomCreateResponseDto dto = new RoomPostDto.RoomCreateResponseDto();
            dto.setRoomPostId(roomPostId);
            return SuccessResponse.onSuccess("모집글이 성공적으로 저장되었습니다.", HttpStatus.CREATED, dto);
        } catch (Exception e) {
            log.error("방 모집글 생성 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(new SuccessResponse<>(HttpStatus.BAD_REQUEST.value(), "요청 처리 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
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
