package com.roommate.roommate.post.service;

import com.roommate.roommate.auth.domain.User;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.common.s3.S3Uploader;
import com.roommate.roommate.post.dto.RoommateCreateRequestDto;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                .orElse(null);


        if (roommatePostRepository.existsByUser_IdAndIsRecruitingTrue(userId) || roomPostRepository.existsByUser_IdAndIsRecruitingTrue(userId)){
            throw new IllegalAccessError("이미 모집글을 작성하셨습니다.");
        }

        String photoUrl = null;
        if (photo != null) {
            photoUrl = s3Uploader.upload(photo);
        }

        RoommatePost roommatePost = RoommatePost.builder()
                .user(user)
                .title(requestDto.getTitle())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .deposit(requestDto.getDeposit())
                .monthlyRent(requestDto.getMonthlyRent())
                .houseType(requestDto.getHouseType())
                .moveInDate(requestDto.getMoveInDate())
                .minStayPeriod(requestDto.getMinStayPeriod())
                .content(requestDto.getContent())
                .photo(photoUrl)
                .area(requestDto.getArea())
                .isRecruiting(true)
                .build();

        roommatePostRepository.save(roommatePost);
        return roommatePost.getRoommatePostId();
    }
}
