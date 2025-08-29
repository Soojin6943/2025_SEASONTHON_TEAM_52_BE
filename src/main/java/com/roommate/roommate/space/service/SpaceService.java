package com.roommate.roommate.space.service;

import com.roommate.roommate.space.dto.SpaceCreateRequest;
import com.roommate.roommate.space.dto.SpaceResponse;
import com.roommate.roommate.space.dto.InviteCodeResponse;
import com.roommate.roommate.space.entity.Space;
import com.roommate.roommate.space.entity.SpaceMember;
import com.roommate.roommate.space.entity.SpaceRole;
import com.roommate.roommate.space.repository.SpaceRepository;
import com.roommate.roommate.space.repository.SpaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;

    @Transactional
    public SpaceResponse createSpace(SpaceCreateRequest request, Long userId) {
        if (request == null) {
            throw new RuntimeException("요청 데이터가 없습니다.");
        }
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new RuntimeException("스페이스 이름을 입력해주세요.");
        }
        
        // 초대 코드 생성 (간단하게 UUID 사용)
        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Space space = Space.builder()
                .name(request.name().trim())
                .inviteCode(inviteCode)
                .build();
        
        Space savedSpace = spaceRepository.save(space);
        
        // 방장을 멤버로 추가
        SpaceMember owner = SpaceMember.builder()
                .space(savedSpace)
                .userId(userId)
                .role(SpaceRole.OWNER)
                .build();
        
        spaceMemberRepository.save(owner);
        
        return SpaceResponse.from(savedSpace);
    }

    public SpaceResponse getMySpace(Long userId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        SpaceMember membership = spaceMemberRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElse(null);
        
        if (membership == null) {
            return null;
        }
        
        return SpaceResponse.from(membership.getSpace());
    }

    public InviteCodeResponse getInviteCode(Long spaceId) {
        if (spaceId == null || spaceId <= 0) {
            throw new RuntimeException("유효하지 않은 스페이스 ID입니다.");
        }
        
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("스페이스를 찾을 수 없습니다. (ID: " + spaceId + ")"));
        
        return new InviteCodeResponse(space.getInviteCode(), space.getName());
    }

    @Transactional
    public SpaceResponse joinSpace(String inviteCode, Long userId) {
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            throw new RuntimeException("초대 코드를 입력해주세요.");
        }
        if (userId == null || userId <= 0) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다.");
        }
        
        Space space = spaceRepository.findByInviteCode(inviteCode.trim())
                .orElseThrow(() -> new RuntimeException("초대 코드가 유효하지 않습니다. (코드: " + inviteCode + ")"));
        
        // 이미 멤버인지 확인
        if (spaceMemberRepository.existsBySpaceIdAndUserId(space.getId(), userId)) {
            throw new RuntimeException("이미 스페이스의 멤버입니다. (사용자 ID: " + userId + ")");
        }
        
        // 현재 멤버 수 조회
        long currentMemberCount = spaceMemberRepository.countBySpaceId(space.getId());
        
        // 멤버 수 제한 확인
        if (currentMemberCount >= space.getMaxMembers()) {
            throw new RuntimeException("스페이스가 가득 찼습니다. 최대 " + space.getMaxMembers() + "명까지 입장 가능합니다.");
        }
        
        // 멤버 추가
        SpaceMember member = SpaceMember.builder()
                .space(space)
                .userId(userId)
                .role(SpaceRole.MEMBER)
                .build();
        
        spaceMemberRepository.save(member);
        
        return SpaceResponse.from(space);
    }
}
