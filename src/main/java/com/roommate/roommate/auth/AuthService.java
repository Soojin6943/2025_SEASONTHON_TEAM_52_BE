package com.roommate.roommate.auth;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.dto.AuthResponse;
import com.roommate.roommate.auth.dto.DetailProfileDto;
import com.roommate.roommate.auth.dto.LoginRequest;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.matching.domain.DesiredProfile;
import com.roommate.roommate.matching.domain.MyProfile;
import com.roommate.roommate.matching.dto.DesiredProfileDto;
import com.roommate.roommate.matching.dto.ProfileDto;
import com.roommate.roommate.matching.repository.MyProfileRepository;
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
    private final MyProfileRepository myProfileRepository;

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

    // 내 성향 프로필 수정
    @Transactional
    public void updateMyProfile(Long userId, ProfileDto profileDto){
        MyProfile myProfile = myProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("MyProfile이 존재하지 않습니다."));

        myProfile.updateMyProfile(profileDto);
    }

    // 사용자 프로필 조회
    @Transactional
    public DetailProfileDto getUserProfile(Long userId) {
        // 1. 유저와 연관된 프로필들을 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다"));

        MyProfile myProfileEntity = user.getMyProfile();
        DesiredProfile desiredProfileEntity = user.getDesiredProfile();

        // 2. MyProfile 엔티티를 ProfileDto로 변환
        ProfileDto myProfileDto = new ProfileDto(
                myProfileEntity.getUser().getId(),
                myProfileEntity.getLifeCycle(),
                myProfileEntity.getSmoking(),
                myProfileEntity.getCleanFreq(),
                myProfileEntity.getTidyLevel(),
                myProfileEntity.getVisitorPolicy(),
                myProfileEntity.getRestroomUsagePattern(),
                myProfileEntity.getFoodOdorPolicy(),
                myProfileEntity.getHomeStay(),
                myProfileEntity.getNoisePreference(),
                myProfileEntity.getSleepSensitivity()
        );

        // 3. DesiredProfile 엔티티를 DesiredProfileDto로 변환
        DesiredProfileDto desiredProfileDto = new DesiredProfileDto(desiredProfileEntity);


        // 4. 올바른 빌더 패턴과 변환된 DTO들을 사용하여 최종 응답 DTO 생성
        //    (오류 수정: new DetailProfileDto().builder() -> DetailProfileDto.builder())
        return DetailProfileDto.builder()
                .name(user.getUsername())
                .age(user.getAge())
                .mbti(user.getMbti())
                .gender(user.getGender())
                .myProfile(myProfileDto)      // 수정된 필드명 적용
                .desiredProfile(desiredProfileDto) // 수정된 필드명 적용
                .build();
    }
}
