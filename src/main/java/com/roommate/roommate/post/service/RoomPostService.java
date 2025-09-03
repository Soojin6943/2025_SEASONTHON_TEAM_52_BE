package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomPostService {

    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;

    public Long createRoomPost(MultipartFile photo, RoomCreateRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        if (roommatePostRepository.existsByUser_IdAndIsRecruitingTrue(userId) || roomPostRepository.existsByUser_IdAndIsRecruitingTrue(userId)){
            throw new IllegalAccessError("이미 모집글을 작성하셨습니다.");
        }

        String photoUrl = null;
        if (photo != null) {
            photoUrl = s3Uploader.upload(photo);
        }

        RoomPost roomPost = RoomPost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .deposit(requestDto.getDeposit())
                .monthlyRent(requestDto.getMonthlyRent())
                .managementFee(requestDto.getManagementFee())
                .houseType(requestDto.getHouseType())
                .size(requestDto.getSize())
                .moveInDate(requestDto.getMoveInDate())
                .minStayPeriod(requestDto.getMinStayPeriod())
                .content(requestDto.getContent())
                .photo(photoUrl)
                .area(requestDto.getArea())
                .gender(user.getGender())
                .isRecruiting(true)
                .build();

        roomPostRepository.save(roomPost);
        return roomPost.getRoomPostId();
    }

    public RoomPostDto.RoomResponseDto getRoomPost(Long roomPostId) {
        RoomPost roomPost = roomPostRepository.findById(roomPostId)
                .orElseThrow();

        RoomPostDto.RoomResponseDto result = RoomPostDto.RoomResponseDto.builder()
                .roomPostId(roomPost.getRoomPostId())
                .userId(roomPost.getUser().getId())
                .username(roomPost.getUser().getUsername())
                .age(roomPost.getUser().getAge())
                .gender(roomPost.getUser().getGender())
                .mbti(roomPost.getUser().getMbti())
                .title(roomPost.getTitle())
                .latitude(roomPost.getLatitude())
                .longitude(roomPost.getLongitude())
                .deposit(roomPost.getDeposit())
                .monthlyRent(roomPost.getMonthlyRent())
                .managementFee(roomPost.getManagementFee())
                .houseType(roomPost.getHouseType())
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
            Integer depositMin, Integer depositMax, Integer rentMin, Integer rentMax, String houseType, MoveInDate moveInDate, Integer minStayPeriod) {
        List<HouseType> houseTypeList;
        if (houseType != null) {
            houseTypeList = Arrays.stream(houseType.split(","))
                    .map(HouseType::valueOf)
                    .toList();
        } else {
            houseTypeList = null;
        }

        List<RoomPost> roomPosts = roomPostRepository.filterPosts(depositMin, depositMax, rentMin, rentMax, houseTypeList, moveInDate, minStayPeriod);

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
}
