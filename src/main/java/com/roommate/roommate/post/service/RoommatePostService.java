package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
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

        Set<HouseType> types = null;
        if (requestDto.getHouseTypes() != null && !requestDto.getHouseTypes().isEmpty()) {
            types = requestDto.getHouseTypes().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        RoommatePost roommatePost = RoommatePost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .deposit(requestDto.getDeposit())
                .monthlyRent(requestDto.getMonthlyRent())
                .houseTypes(types)
                .moveInDate(requestDto.getMoveInDate())
                .minStayPeriod(requestDto.getMinStayPeriod())
                .content(requestDto.getContent())
                .photo(photoUrl)
                .area(requestDto.getArea())
                .gender(user.getGender())
                .isRecruiting(true)
                .build();

        roommatePostRepository.save(roommatePost);
        return roommatePost.getRoommatePostId();
    }

    public RoommatePostDto.RoommateResponseDto getRoommatePost(Long roommatePostId) {
        RoommatePost roommatePost = roommatePostRepository.findById(roommatePostId)
                .orElseThrow();

        List<HouseType> types = roommatePost.getHouseTypes()
                .stream()
                .toList();

        RoommatePostDto.RoommateResponseDto result = RoommatePostDto.RoommateResponseDto.builder()
                .roommatePostId(roommatePostId)
                .userId(roommatePost.getUser().getId())
                .username(roommatePost.getUser().getUsername())
                .age(roommatePost.getUser().getAge())
                .gender(roommatePost.getUser().getGender())
                .mbti(roommatePost.getUser().getMbti())
                .title(roommatePost.getTitle())
                .latitude(roommatePost.getLatitude())
                .longitude(roommatePost.getLongitude())
                .deposit(roommatePost.getDeposit())
                .monthlyRent(roommatePost.getMonthlyRent())
                .houseTypes(types)
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
            Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }

        List<RoommatePost> roommatePosts = roommatePostRepository.filterPosts(depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

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
                    .build();
            list.add(listDto);
        }

        RoommatePostDto.RoommateList result = new RoommatePostDto.RoommateList();
        result.setPosts(list);
        return result;
    }
}
