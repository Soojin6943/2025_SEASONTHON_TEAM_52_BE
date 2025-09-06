package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.location.service.CoordinateService;
import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.matching.MatchingService;
import com.roommate.roommate.matching.RecommendationService;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.repository.DesiredProfileRepository;
import com.roommate.roommate.matching.repository.MyProfileRepository;
import com.roommate.roommate.post.dto.MatchedOptionsDto;
import com.roommate.roommate.post.dto.RoommateCreateRequestDto;
import com.roommate.roommate.post.dto.RoommateListDto;
import com.roommate.roommate.post.dto.RoommatePostDto;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoommatePostService {

    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final CoordinateService coordinateService;
    private final RecommendationService recommendationService;
    private final MatchingService matchingService;
    private final DesiredProfileRepository desiredProfileRepository;
    private final MyProfileRepository myProfileRepository;

    @Transactional
    public Long createRoommatePost(MultipartFile photo, RoommateCreateRequestDto requestDto, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow();

        if (roommatePostRepository.existsByUser_IdAndIsRecruitingTrue(userId) || roomPostRepository.existsByUser_IdAndIsRecruitingTrue(userId)){
            throw new IllegalAccessError("이미 모집글을 작성하셨습니다.");
        }

        String photoUrl = null;
        if (photo != null) {
            photoUrl = s3Uploader.upload(photo);
        }

        HouseType houseType = requestDto.getHouseType();

        LocationInfo locationInfo = coordinateService.findLocationByCoordinates(
                requestDto.getLongitude(), 
                requestDto.getLatitude()
        );

        //좌표에 오프셋 적용하여 저장
        LocationInfo offsetLocation = coordinateService.applyCoordinateOffset(
                requestDto.getLongitude(), 
                requestDto.getLatitude()
        );

        RoommatePost roommatePost = RoommatePost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .latitude(offsetLocation.getLatitude())
                .longitude(offsetLocation.getLongitude())
                .deposit(requestDto.getDeposit())
                .monthlyRent(requestDto.getMonthlyRent())
                .houseType(houseType)
                .moveInDate(requestDto.getMoveInDate())
                .minStayPeriod(requestDto.getMinStayPeriod())
                .content(requestDto.getContent())
                .photo(photoUrl)
                .area(locationInfo.getFullAddress())
                .gu_name(locationInfo.getGuName())
                .dong_name(locationInfo.getDongName())
                .bjcd(locationInfo.getGuCode())
                .cmd_cd(locationInfo.getDongCode())
                .gender(user.getGender())
                .isRecruiting(true)
                .build();

        roommatePostRepository.save(roommatePost);

        user.setActive(true);
        userRepository.save(user);

        return roommatePost.getRoommatePostId();
    }

    public RoommatePostDto.RoommateResponseDto getRoommatePost(Long userId, Long roommatePostId) {
        RoommatePost roommatePost = roommatePostRepository.findById(roommatePostId)
                .orElseThrow();

        HouseType houseType = roommatePost.getHouseType();

        MatchedOptionsDto matchesFromLogin = matchingService.getMatchedOptions(Objects.requireNonNull(desiredProfileRepository.findByUserId(userId).orElse(null)), Objects.requireNonNull(myProfileRepository.findByUserId(roommatePost.getUser().getId()).orElse(null)));
        MatchedOptionsDto matchesFromPost = matchingService.getMatchedOptions(Objects.requireNonNull(desiredProfileRepository.findByUserId(roommatePost.getUser().getId()).orElse(null)), Objects.requireNonNull(myProfileRepository.findByUserId(userId).orElse(null)));

        MatchedOptionsDto matchedOptionsDto = recommendationService.checkMatchedOptions(matchesFromLogin, matchesFromPost);

        RoommatePostDto.RoommateResponseDto result = RoommatePostDto.RoommateResponseDto.builder()
                .roommatePostId(roommatePostId)
                .userId(roommatePost.getUser().getId())
                .username(roommatePost.getUser().getUsername())
                .age(roommatePost.getUser().getAge())
                .gender(roommatePost.getUser().getGender())
                .mbti(roommatePost.getUser().getMbti())
                .matchedOptions(matchedOptionsDto)
                .title(roommatePost.getTitle())
                .latitude(roommatePost.getLatitude())
                .longitude(roommatePost.getLongitude())
                .deposit(roommatePost.getDeposit())
                .monthlyRent(roommatePost.getMonthlyRent())
                .houseType(houseType)
                .moveInDate(roommatePost.getMoveInDate())
                .minStayPeriod(roommatePost.getMinStayPeriod())
                .content(roommatePost.getContent())
                .photo(roommatePost.getPhoto())
                .area(roommatePost.getArea())
                .date(roommatePost.getCreatedAt().toLocalDate())
                .build();

        return result;
    }

    public RoommatePostDto.RoommateList getRoommatePosts(
            String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }

        List<RoommatePost> roommatePosts = roommatePostRepository.filterPosts(area, depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

        List<RoommateListDto> list = new ArrayList<>();
        for (RoommatePost roommatePost : roommatePosts) {
            RoommateListDto listDto = RoommateListDto.builder()
                    .roommatePostId(roommatePost.getRoommatePostId())
                    .userId(roommatePost.getUser().getId())
                    .username(roommatePost.getUser().getUsername())
                    .userProfile(roommatePost.getUser().getProfileImageUrl())
                    .age(roommatePost.getUser().getAge())
                    .title(roommatePost.getTitle())
                    .deposit(roommatePost.getDeposit())
                    .monthlyRent(roommatePost.getMonthlyRent())
                    .houseType(roommatePost.getHouseType())
                    .build();
            list.add(listDto);
        }

        RoommatePostDto.RoommateList result = new RoommatePostDto.RoommateList();
        result.setPosts(list);
        return result;
    }

    public RoommatePostDto.RoommateList getMatchingRoommatePosts(
            Long userId, String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }
        List<RecommendationDto> recommendDtos = recommendationService.getRecommendations(userId, area, false);
        List<Long> userIds = new ArrayList<>();
        for (RecommendationDto recommendDto : recommendDtos) {
            Long id = recommendDto.getUserId();
            userIds.add(id);
        }

        List<RoommatePost> roommatePosts = roommatePostRepository.filterPosts(area, depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            orderMap.put(userIds.get(i), i);
        }
        List<RoommatePost> sortedPosts = roommatePosts.stream()
                .filter(post -> orderMap.containsKey(post.getUser().getId()))
                .sorted(Comparator.comparingInt(post -> orderMap.get(post.getUser().getId())))
                .collect(Collectors.toList());

        List<RoommateListDto> list = new ArrayList<>();
        for (RoommatePost sortedPost : sortedPosts) {
            RoommateListDto listDto = RoommateListDto.builder()
                    .roommatePostId(sortedPost.getRoommatePostId())
                    .userId(sortedPost.getUser().getId())
                    .username(sortedPost.getUser().getUsername())
                    .userProfile(sortedPost.getUser().getProfileImageUrl())
                    .age(sortedPost.getUser().getAge())
                    .title(sortedPost.getTitle())
                    .deposit(sortedPost.getDeposit())
                    .monthlyRent(sortedPost.getMonthlyRent())
                    .houseType(sortedPost.getHouseType())
                    .build();
            list.add(listDto);
        }

        RoommatePostDto.RoommateList result = new RoommatePostDto.RoommateList();
        result.setPosts(list);
        return result;

    }
}
