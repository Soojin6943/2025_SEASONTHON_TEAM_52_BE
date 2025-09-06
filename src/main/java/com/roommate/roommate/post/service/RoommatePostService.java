package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.location.service.CoordinateService;
import com.roommate.roommate.location.dto.LocationInfo;
import com.roommate.roommate.matching.MatchingService;
import com.roommate.roommate.matching.RecommendationService;
import com.roommate.roommate.matching.dto.RecommendationDto;
import com.roommate.roommate.matching.dto.RoommatePostRecommendationDto;
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

    public List<RoommatePostRecommendationDto> getMatchingRoommatePosts(
            Long userId, String area, Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<RecommendationDto> recs = recommendationService.getRecommendations(userId, area, false);
        if (recs.isEmpty()) return List.of();

        Map<Long, Integer> order = new HashMap<>();
        Map<Long, RecommendationDto> recByUser = new HashMap<>();
        for (int i = 0; i < recs.size(); i++) {
            RecommendationDto r = recs.get(i);
            order.put(r.getUserId(), i);
            recByUser.put(r.getUserId(), r);
        }

        // 2) houseType 파싱 (위 로직 유지 + 공백 트림 보강)
        List<HouseType> houseTypeList = null;
        if (houseType != null && !houseType.isBlank()) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(HouseType::valueOf)
                    .collect(Collectors.toList());
            if (houseTypeList.isEmpty()) houseTypeList = null;
        }

        // 3) 필터 적용해 게시글 조회
        List<RoommatePost> roommatePosts = roommatePostRepository.filterPosts(
                area, depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

        // 4) 추천 유저와 교집합만 남기고, 추천순(order)으로 정렬 후 "유저당 1개" 게시글만 선택
        List<RoommatePost> sortedDistinctByUser = roommatePosts.stream()
                .filter(p -> order.containsKey(p.getUser().getId()))
                .sorted(Comparator.comparingInt(p -> order.get(p.getUser().getId())))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                p -> p.getUser().getId(),
                                p -> p,
                                (p1, p2) -> p1,          // 같은 유저의 다수 글이 있으면 추천순에서 먼저 온 것 채택
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));

        // 5) 반환 DTO 매핑 (첫 번째 메서드와 동일한 형태)
        List<RoommatePostRecommendationDto> results = new ArrayList<>();
        for (RoommatePost p : sortedDistinctByUser) {
            Long uid = p.getUser().getId();
            User u = p.getUser();
            RecommendationDto r = recByUser.get(uid);

            RoommatePostRecommendationDto dto = RoommatePostRecommendationDto.builder()
                    .roommatePostId(p.getRoommatePostId())
                    .userId(uid)
                    .username(u.getUsername())
                    .userProfile(u.getProfileImageUrl())
                    .age(u.getAge())
                    .mbti(u.getMbti())
                    .score(r != null ? r.getAverageScore() : 0.0)
                    .matchedOptions(r != null ? r.getMatchedOptions() : null)
                    .title(p.getTitle())
                    .deposit(p.getDeposit())
                    .monthlyRent(p.getMonthlyRent())
                    .build();

            results.add(dto);
        }

        return results;
    }
}
