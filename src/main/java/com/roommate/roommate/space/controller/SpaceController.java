package com.roommate.roommate.space.controller;

import com.roommate.roommate.space.dto.SpaceCreateRequest;
import com.roommate.roommate.space.dto.SpaceResponse;
import com.roommate.roommate.space.dto.InviteCodeResponse;
import com.roommate.roommate.space.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Space")
@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(summary = "스페이스 생성")
    @PostMapping
    public ResponseEntity<SpaceResponse> createSpace(@RequestBody @Valid SpaceCreateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(spaceService.createSpace(request, userId));
    }

    @Operation(summary = "내 스페이스 조회")
    @GetMapping("/my")
    public ResponseEntity<SpaceResponse> getMySpace(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        SpaceResponse space = spaceService.getMySpace(userId);
        if (space == null) {
            return ResponseEntity.status(404).build();
        }
        
        return ResponseEntity.ok(space);
    }

    @Operation(summary = "초대 코드 조회")
    @GetMapping("/{spaceId}/invite-code")
    public ResponseEntity<InviteCodeResponse> getInviteCode(@PathVariable Long spaceId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(spaceService.getInviteCode(spaceId));
    }

    @Operation(summary = "초대 코드로 스페이스 입장")
    @PostMapping("/join")
    public ResponseEntity<SpaceResponse> joinSpace(@RequestParam String inviteCode, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        return ResponseEntity.ok(spaceService.joinSpace(inviteCode, userId));
    }
}
