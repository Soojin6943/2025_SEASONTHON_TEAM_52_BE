package com.roommate.roommate.post.entity;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoommatePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roommatePostId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = true)
    private Integer deposit;

    @Column(nullable = true)
    private Integer monthlyRent;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "roommate_post_house_type",
            joinColumns = @JoinColumn(name = "roommate_post_id")
    )
    @Column(nullable = true)
    private Set<HouseType> houseTypes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MoveInDate moveInDate;

    @Column(nullable = true)
    private Integer minStayPeriod;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String photo;

    @Column(nullable = false)
    private String area;

    @Column(nullable = true)
    private String gu_name;

    @Column(nullable = true)
    private String dong_name;

    @Column(nullable = true)
    private String bjcd;

    @Column(nullable = true)
    private String cmd_cd;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private boolean isRecruiting;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
