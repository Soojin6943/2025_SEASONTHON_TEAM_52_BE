package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.AuthResponse;
import com.roommate.roommate.auth.dto.LoginRequest;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.space.repository.SpaceMemberRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final S3Uploader s3Uploader;

    // 로그인 (없으면 회원가입)
    @Transactional
    public User loginOrSignUp(LoginRequest req) {
        return userRepository.findByUsername(req.username())
                .orElseGet(() -> createNewUser(req.username()));
    }

    // 처음 로그인 시 자동 회원가입
    private User createNewUser(String name){

        // 랜덤 나이 값
        Random random = new Random();
        int randomAge = (random.nextInt(15)+20);

        // 랜덤 성별
        Gender randomGender = random.nextBoolean() ? Gender.MALE : Gender.FEMALE;

        // 첫 로그인 한 다음에는 비활성화 (아직 공고를 올리지 않았기 때문)
        boolean isActive = false;

        User newUser = User.builder()
                .username(name)
                .age(randomAge)
                .gender(randomGender)
                .isActive(isActive)
                .hasSpace(false) // 기본적으로 스페이스에 소속되지 않음
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Transactional
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    // 사용자의 스페이스 소속 여부를 실제 데이터에서 조회하여 업데이트
    @Transactional
    public void updateUserSpaceStatusFromDatabase(Long userId) {
        User user = findById(userId);
        // 더 명확한 쿼리 사용
        boolean hasSpace = spaceMemberRepository.userExistsInAnySpace(userId);
        user.updateHasSpace(hasSpace);
        
        // 디버깅을 위한 로그 (나중에 제거 가능)
        System.out.println("User " + userId + " hasSpace: " + hasSpace);
    }
    

    // 오픈 채팅 링크 추가
    @Transactional
    public  void setChatLink(Long userId, String link){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
        user.updateKakaoOpenChatLink(link);
    }

    // 유저 프로필 사진 추가
    // 기존 이미지가 있다면 S3에서 삭제
    @Transactional
    public String updateProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 아이디를 찾을 수 없습니다"));

        // 기존 프로필 이미지가 있다면 S3에서 삭제
        String oldImageUrl = user.getProfileImageUrl();
        if(oldImageUrl != null) {
            s3Uploader.delete(oldImageUrl);
        }

        // 새 이미지 s3 업로드 후 url 받음
        String newImageUrl = s3Uploader.upload(imageFile);

        // 사용자 정보에 새 이미지 url 업로드
        user.updateProfileImage(newImageUrl);

        return newImageUrl;
    }
}
