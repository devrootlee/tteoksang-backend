package com.example.tteoksang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_profile")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class MemberProfile {
    @Id
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
