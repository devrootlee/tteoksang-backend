package com.example.tteoksang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_local")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class MemberLocal {
    @Id
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "local_id", nullable = false)
    private String localId;

    @Column(name = "local_password", nullable = false)
    private String localPassword;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
