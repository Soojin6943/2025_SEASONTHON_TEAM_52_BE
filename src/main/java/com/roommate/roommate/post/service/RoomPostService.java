package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.location.service.CoordinateService;
import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.matching.MatchingService;
import com.roommate.roommate.matching.RecommendationService;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.dto.RoomPostRecommendationDto;
import com.roommate.roommate.matching.dto.RoommatePostRecommendationDto;
import com.roommate.roommate.matching.repository.DesiredProfileRepository;
import com.roommate.roommate.matching.repository.MyProfileRepository;
import com.roommate.roommate.post.dto.MatchedOptionsDto;
import com.roommate.roommate.post.dto.RoomCreateRequestDto;
import com.roommate.roommate.post.dto.RoomListDto;
import com.roommate.roommate.post.dto.RoomPostDto;
import com.roommate.roommate.post.entity.HouseType;
import com.roommate.roommate.post.entity.MoveInDate;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomPostService {

    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final CoordinateService coordinateService;
    private final RecommendationService recommendationService;
    private final MatchingService matchingService;
    private final DesiredProfileRepository desiredProfileRepository;
    private final MyProfileRepository myProfileRepository;

    @Transactional
    public Long createRoomPost(MultipartFile photo, RoomCreateRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        if (roommatePostRepository.existsByUser_IdAndIsRecruitingTrue(userId) || roomPostRepository.existsByUser_IdAndIsRecruitingTrue(userId)){
            throw new IllegalAccessError("이미 모집글을 작성하셨습니다.");
        }

        String photoUrl = null;
        if (photo != null) {
            photoUrl = s3Uploader.upload(photo);
        }

        LocationInfo locationInfo = coordinateService.findLocationByCoordinates(
                requestDto.getLongitude(), 
                requestDto.getLatitude()
        );

        //좌표에 오프셋 적용하여 저장데이터는는
        LocationInfo offsetLocation = coordinateService.applyCoordinateOffset(
                requestDto.getLongitude(), 
                requestDto.getLatitude()
        );

        RoomPost roomPost = RoomPost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .latitude(offsetLocation.getLatitude())
                .longitude(offsetLocation.getLongitude())
                .deposit(requestDto.getDeposit())
                .monthlyRent(requestDto.getMonthlyRent())
                .managementFee(requestDto.getManagementFee())
                .houseType(requestDto.getHouseType())
                .roomNum(requestDto.getRoomNum())
                .size(requestDto.getSize())
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

        roomPostRepository.save(roomPost);

        user.setActive(true);
        userRepository.save(user);

        return roomPost.getRoomPostId();
    }

    public RoomPostDto.RoomResponseDto getRoomPost(Long userId, Long roomPostId) {
        RoomPost roomPost = roomPostRepository.findById(roomPostId)
                .orElseThrow();

        MatchedOptionsDto matchesFromLogin = matchingService.getMatchedOptions(Objects.requireNonNull(desiredProfileRepository.findByUserId(userId).orElse(null)), Objects.requireNonNull(myProfileRepository.findByUserId(roomPost.getUser().getId()).orElse(null)));
        MatchedOptionsDto matchesFromPost = matchingService.getMatchedOptions(Objects.requireNonNull(desiredProfileRepository.findByUserId(roomPost.getUser().getId()).orElse(null)), Objects.requireNonNull(myProfileRepository.findByUserId(userId).orElse(null)));

        MatchedOptionsDto matchedOptionsDto = recommendationService.checkMatchedOptions(matchesFromLogin, matchesFromPost);

        RoomPostDto.RoomResponseDto result = RoomPostDto.RoomResponseDto.builder()
                .roomPostId(roomPost.getRoomPostId())
                .userId(roomPost.getUser().getId())
                .username(roomPost.getUser().getUsername())
                .age(roomPost.getUser().getAge())
                .gender(roomPost.getUser().getGender())
                .mbti(roomPost.getUser().getMbti())
                .matchedOptions(matchedOptionsDto)
                .title(roomPost.getTitle())
                .latitude(roomPost.getLatitude())
                .longitude(roomPost.getLongitude())
                .deposit(roomPost.getDeposit())
                .monthlyRent(roomPost.getMonthlyRent())
                .managementFee(roomPost.getManagementFee())
                .houseType(roomPost.getHouseType())
                .roomNum(roomPost.getRoomNum())
                .size(roomPost.getSize())
                .moveInDate(roomPost.getMoveInDate())
                .minStayPeriod(roomPost.getMinStayPeriod())
                .content(roomPost.getContent())
                .photo(roomPost.getPhoto())
                .area(roomPost.getArea())
                .date(roomPost.getCreatedAt().toLocalDate())
                .build();

        return result;
    }

    public RoomPostDto.RoomList getRoomPosts(
            String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }

        List<RoomPost> roomPosts = roomPostRepository.filterPosts(area, depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

        List<RoomListDto> list = new ArrayList<>();
        for (RoomPost roomPost : roomPosts) {
            RoomListDto listDto = RoomListDto.builder()
                    .roomPostId(roomPost.getRoomPostId())
                    .userId(roomPost.getUser().getId())
                    .username(roomPost.getUser().getUsername())
                    .userProfile(roomPost.getUser().getProfileImageUrl())
                    .age(roomPost.getUser().getAge())
                    .title(roomPost.getTitle())
                    .deposit(roomPost.getDeposit())
                    .monthlyRent(roomPost.getMonthlyRent())
                    .build();
            list.add(listDto);
        }

        RoomPostDto.RoomList result = new RoomPostDto.RoomList();
        result.setPosts(list);
        return result;
    }
    public List<RoomPostRecommendationDto> getMatchingRoomPosts(Long userId, String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {

        List<RecommendationDto> recs = recommendationService.getRecommendations(userId, area, true);
        if (recs.isEmpty()) return List.of();

        Map<Long, Integer> order = new HashMap<>();
        Map<Long, RecommendationDto> recByUser = new HashMap<>();
        for (int i = 0; i < recs.size(); i++) {
            RecommendationDto r = recs.get(i);
            order.put(r.getUserId(), i);
            recByUser.put(r.getUserId(), r);
        }

        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }

        List<RoomPost> roomPosts = roomPostRepository.filterPosts(area, depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

        List<RoomPost> sortedPosts = roomPosts.stream()
                .filter(post -> order.containsKey(post.getUser().getId()))
                .sorted(Comparator.comparingInt(post -> order.get(post.getUser().getId())))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                p -> p.getUser().getId(),
                                p -> p,
                                (p1, p2) -> p1,          // 같은 유저의 다수 글이 있으면 추천순에서 먼저 온 것 채택
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));

        List<RoomPostRecommendationDto> list = new ArrayList<>();
        for (RoomPost p : sortedPosts) {
            Long uid = p.getUser().getId();
            User u = p.getUser();
            RecommendationDto r = recByUser.get(uid);

            RoomPostRecommendationDto listDto = RoomPostRecommendationDto.builder()
                    .roomPostId(p.getRoomPostId())
                    .userId(uid)
                    .username(u.getUsername())
                    .userProfile(u.getProfileImageUrl())
                    .age(u.getAge())
                    .score(r != null ? r.getAverageScore() : 0.0)
                    .matchedOptions(r != null ? r.getMatchedOptions() : null)
                    .title(p.getTitle())
                    .deposit(p.getDeposit())
                    .monthlyRent(p.getMonthlyRent())
                    .build();
            list.add(listDto);
        }

        return list;
    }
}
