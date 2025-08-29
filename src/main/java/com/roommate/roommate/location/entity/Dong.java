package com.roommate.roommate.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dong")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dong_name", nullable = false, length = 100)
    private String dongName;
    
    @Column(name = "emd_cd", nullable = false, length = 10)
    private String emdCd;
    
    @Column(name = "bjcd", nullable = false, length = 10)
    private String bjcd;
    
    @Column(name = "geom", columnDefinition = "TEXT")
    private String geom;
}
