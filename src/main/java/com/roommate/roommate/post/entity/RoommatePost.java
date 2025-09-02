package com.roommate.roommate.post.entity;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private HouseType houseType;

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

    @Column(nullable = false)
    private boolean isRecruiting;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
