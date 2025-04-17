package com.example.tteoksang.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kis_token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KisToken {
    @Id
    private int id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
