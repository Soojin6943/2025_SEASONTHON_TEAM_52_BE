package com.roommate.roommate.matching;

import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.matching.dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
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

}
