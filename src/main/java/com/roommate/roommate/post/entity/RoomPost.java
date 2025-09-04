package com.roommate.roommate.post.entity;

import com.roommate.roommate.auth.domain.Gender;
import com.roommate.roommate.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomPostId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer deposit;

    @Column(nullable = false)
    private Integer monthlyRent;

    @Column(nullable = false)
    private Integer managementFee;

    @Column(nullable = false)
    private HouseType houseType;

    @Column(nullable = false)
    private Double size;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private boolean isRecruiting = true;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
