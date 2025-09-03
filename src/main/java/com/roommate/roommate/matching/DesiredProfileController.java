package com.roommate.roommate.matching;

import com.roommate.roommate.common.SuccessResponse;
import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.dto.DesiredProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이상형 프로필")
@RestController
@RequestMapping("/desired-profile")
@RequiredArgsConstructor
public class DesiredProfileController {
    private final DesiredProfileService desiredProfileService;

    // 프로필 생성
    @Operation(summary = "이상형 프로필 생성")
    @PostMapping
    public ResponseEntity<SuccessResponse<DesiredProfile>> create(@SessionAttribute("userId") Long userId, @RequestBody DesiredProfileDto dto){

        // 생성
        DesiredProfile profile = desiredProfileService.create(userId, dto);

        return SuccessResponse.onSuccess("이상형 프로필이 생성되었습니다.", HttpStatus.CREATED, profile);
    }
}
