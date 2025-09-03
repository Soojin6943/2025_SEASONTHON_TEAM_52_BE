package com.roommate.roommate.application.service;

import com.roommate.roommate.application.dto.ApplicationConfirmDto;
import com.roommate.roommate.application.dto.ApplicationListDto;
import com.roommate.roommate.application.entity.Application;
import com.roommate.roommate.application.entity.Status;
import com.roommate.roommate.application.repository.ApplicationRepository;
import com.roommate.roommate.auth.UserRepository;
import com.roommate.roommate.post.entity.RoomPost;
import com.roommate.roommate.post.entity.RoommatePost;
import com.roommate.roommate.post.repository.RoomPostRepository;
import com.roommate.roommate.post.repository.RoommatePostRepository;
import kotlin.io.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final RoommatePostRepository roommatePostRepository;
    private final RoomPostRepository roomPostRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public Long applyPost(Long userId, Long roommatePostId, Long roomPostId) {
//        // 만약 이미 같은 게시글에 지원한상태라면 < 지원 불가!! 막 이런거 떠야될거 같은데 아 어렵드ㅏ...
//        // 그 게시글이 IsRecruiting 중이여야됨! <<check
//
//        RoommatePost rmp = roommatePostRepository.findById(roommatePostId).orElse(null);
//        RoomPost rp = roomPostRepository.findById(roomPostId).orElse(null);
//
//        if (!rmp.isRecruiting() && !rp.isRecruiting())
//            throw new IllegalArgumentException("모집중인 글이 아닙니다.");

        RoommatePost rmp = roommatePostRepository.findById(roommatePostId).orElse(null);
        RoomPost rp = roomPostRepository.findById(roomPostId).orElse(null);
        Application application = Application.builder()
                .roommatePost(rmp)
                .roomPost(rp)
                .user(userRepository.findById(userId).orElse(null))
                .status(Status.AWAITING)
                .build();
        applicationRepository.save(application);

        return application.getApplicationId();
    }

    public List<ApplicationListDto> getMyApplication(Long userId){
        List<Application> applications = applicationRepository.findByUser_IdOrderByApplicationIdDesc(userId);
        List<ApplicationListDto> response = new ArrayList<>();
        for (Application application : applications) {
            RoommatePost rmp = application.getRoommatePost();
            RoomPost rp = application.getRoomPost();
            ApplicationListDto dto = ApplicationListDto.builder()
                    .applicationId(application.getApplicationId())
                    .roommatePostId((rmp == null) ? null : rmp.getRoommatePostId())
                    .roomPostId((rp == null) ? null : rp.getRoomPostId())
                    .userId(application.getUser().getId())
                    .userProfile(application.getUser().getProfileImageUrl())
                    .username(application.getUser().getUsername())
                    .age(application.getUser().getAge())
                    .gender(application.getUser().getGender())
                    .status(application.getStatus())
                    .build();
            response.add(dto);
        }
        return response;
    }

    public List<ApplicationListDto> getPostApplication(Long userId){
        RoommatePost roommatePost = roommatePostRepository.findByUser_IdAndIsRecruitingTrue(userId).orElse(null);
        RoomPost roomPost = roomPostRepository.findByUser_IdAndIsRecruitingTrue(userId).orElse(null);

        List<Application> applications = new ArrayList<>();
        if (roommatePost != null)
            applications = applicationRepository.findByRoommatePost(roommatePost);
        if (roomPost != null)
            applications = applicationRepository.findByRoomPost(roomPost);

        List<ApplicationListDto> response = new ArrayList<>();
        for (Application application : applications) {
            ApplicationListDto dto = ApplicationListDto.builder()
                    .applicationId(application.getApplicationId())
                    .roommatePostId( (roommatePost == null) ? null : roommatePost.getRoommatePostId())
                    .roomPostId( (roomPost == null) ? null : roomPost.getRoomPostId())
                    .userId(application.getUser().getId())
                    .userProfile(application.getUser().getProfileImageUrl())
                    .username(application.getUser().getUsername())
                    .age(application.getUser().getAge())
                    .gender(application.getUser().getGender())
                    .status(application.getStatus())
                    .build();
            response.add(dto);
        }
        return response;
    }

    public void cancelApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow();

        if (!application.getUser().getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 글을 지울 수 없습니다.");

        application.setStatus(Status.CANCELED);
        applicationRepository.save(application);
    }

    public void acceptApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow();

        if (application.getRoommatePost() != null && !application.getRoommatePost().getRoommatePostId().equals(userId) || application.getRoomPost() != null && !application.getRoomPost().getRoomPostId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 글을 수락할 수 없습니다.");

        application.setStatus(Status.ACCEPTED);
        applicationRepository.save(application);
    }

    public void rejectApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow();

        if (application.getRoommatePost() != null && !application.getRoommatePost().getRoommatePostId().equals(userId) || application.getRoomPost() != null && !application.getRoomPost().getRoomPostId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 글을 수락할 수 없습니다.");

        application.setStatus(Status.REJECTED);
        applicationRepository.save(application);
    }

    public ApplicationConfirmDto confirmApplication(Long applicationId, Long userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow();

        if (application.getRoommatePost() != null && !application.getRoommatePost().getRoommatePostId().equals(userId) || application.getRoomPost() != null && !application.getRoomPost().getRoomPostId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 글을 수락할 수 없습니다.");

        application.setStatus(Status.CONFIRMED);

        if (application.getRoomPost() != null){
            application.getRoomPost().setRecruiting(false);
            roomPostRepository.save(application.getRoomPost());
        }
        if (application.getRoommatePost() != null){
            application.getRoommatePost().setRecruiting(false);
            roommatePostRepository.save(application.getRoommatePost());
        }

        applicationRepository.save(application);

        ApplicationConfirmDto result = ApplicationConfirmDto.builder()
                .applicationId(application.getApplicationId())
                .roommatePostId((application.getRoommatePost() == null) ? null : application.getRoommatePost().getRoommatePostId())
                .roomPostId((application.getRoomPost() == null) ? null : application.getRoomPost().getRoomPostId())
                .build();

        return result;
    }

}
