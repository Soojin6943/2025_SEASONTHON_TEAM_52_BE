package com.roommate.roommate.matching.domain;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class TestPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 (User와 1:1 관계)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 모집 지역 (User에 있는 필드를 사용해도 되지만, 게시물에 별도로 두는 것을 가정)
    private String area;

    // 성별 (User의 성별과 동일하게 저장)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 모집 상태 (공고 활성화 여부)
    private boolean isRecruiting;

    public TestPost() {}

    public static TestPost createTestPost(User user, String area, boolean isRecruiting) {
        TestPost post = new TestPost();
        post.user = user;
        post.area = area;
        post.gender = user.getGender();
        post.isRecruiting = isRecruiting;
        return post;
    }
}
