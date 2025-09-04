package com.roommate.roommate.matching;

import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.dto.RoomPostRecommendationDto;
import com.roommate.roommate.matching.dto.RoommatePostRecommendationDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;

    // GET /recommendations?location=서울시 마포구
    @GetMapping
    public ResponseEntity<SuccessResponse<List<RecommendationDto>>> getRecommendations(
            @SessionAttribute(name = "userId") Long userId,
            @RequestParam("location") String location) { // 1. @RequestParam으로 location 받기

        // 2. 서비스 호출 시 userId와 location 함께 전달
        List<RecommendationDto> recommendations = recommendationService.getRecommendations(userId, location);

        return SuccessResponse.onSuccess("룸메이트 추천 목록 조회에 성공했습니다.", HttpStatus.OK, recommendations);
    }

    @GetMapping("/roommate-posts")
    public ResponseEntity<SuccessResponse<List<RoommatePostRecommendationDto>>> getRoommateRecommendations(
            HttpSession session,
            @RequestParam(value = "area", required = true) String area
    ){
        Long userId = (Long)session.getAttribute("userId");
        List<RoommatePostRecommendationDto> dto = recommendationService.getRoommateRecommendations(userId, area);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.", HttpStatus.OK, dto);
    }

    @GetMapping("/room-posts")
    public ResponseEntity<SuccessResponse<List<RoomPostRecommendationDto>>> getRoommatePosts(
            HttpSession session,
            @RequestParam(value = "area", required = true) String area
    ){
        Long userId = (Long)session.getAttribute("userId");
        List<RoomPostRecommendationDto> dto = recommendationService.getRoomRecommendations(userId, area);
        return SuccessResponse.onSuccess("모집글들을 성공적으로 조회했습니다.",  HttpStatus.OK, dto);
    }
}
