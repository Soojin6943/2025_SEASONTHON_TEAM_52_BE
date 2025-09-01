package com.roommate.roommate.matching.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roommate.roommate.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class MyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지연 로딩 LAZY
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}
