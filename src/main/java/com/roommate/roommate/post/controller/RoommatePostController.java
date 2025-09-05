package com.roommate.roommate.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.post.dto.RoommateCreateRequestDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.service.RoommatePostService;
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
@RequestMapping("/api/roommate-posts")
@RequiredArgsConstructor
@Slf4j
public class RoommatePostController {

    private final RoommatePostService roommatePostService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "룸메이트 모집글 생성", description = "사진과 함께 룸메이트 모집글을 생성합니다. 좌표값으로부터 지역 정보가 자동으로 설정됩니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<RoommatePostDto.RoommateCreateResponseDto>> createRoommatePost(
            HttpSession session,
            @RequestPart(value = "photo", required = false)
            @Schema(type = "string", format = "binary", description = "업로드할 사진 파일 (선택사항)")
            MultipartFile photo,
            @RequestPart(value = "meta", required = true) 
            @Schema(description = "룸메이트 모집글 메타데이터 (JSON 문자열)", 
                    example = "{\"title\":\"깔끔한 원룸에서 룸메이트 구해요\",\"content\":\"강남구 역삼동 원룸에서 룸메이트를 구합니다.\",\"latitude\":37.5665,\"longitude\":126.9780,\"deposit\":1000,\"monthlyRent\":50,\"houseType\":\"APARTMENT\",\"moveInDate\":\"Q1\",\"minStayPeriod\":6}") 
            String metaJson) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            
            RoommateCreateRequestDto requestDto = objectMapper.readValue(metaJson, RoommateCreateRequestDto.class);
            
            Long roommatePostId = roommatePostService.createRoommatePost(photo, requestDto, userId);
            RoommatePostDto.RoommateCreateResponseDto dto = new RoommatePostDto.RoommateCreateResponseDto();
            dto.setRoommatePostId(roommatePostId);
            return SuccessResponse.onSuccess("모집글이 성공적으로 저장되었습니다.", HttpStatus.CREATED, dto);
        } catch (Exception e) {
            log.error("룸메이트 모집글 생성 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(new SuccessResponse<>(HttpStatus.BAD_REQUEST.value(), "요청 처리 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
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
        RoommatePostDto.RoommateList dto = roommatePostService.getMatchingRoommatePosts(userId, area, depositMin, depositMax, rentMin, rentMax, houseType, moveInDate, minStay);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }
}

