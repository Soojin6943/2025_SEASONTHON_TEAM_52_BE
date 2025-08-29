package com.roommate.roommate.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gu")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gu_name", nullable = false, length = 100)
    private String guName;
    
    @Column(name = "bjcd", nullable = false, length = 10)
    private String bjcd;
    
    @Column(name = "geom", columnDefinition = "TEXT")
    private String geom;
}
