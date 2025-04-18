package com.example.tteoksang.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "member_type", nullable = false)
    private MemberType memberType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
