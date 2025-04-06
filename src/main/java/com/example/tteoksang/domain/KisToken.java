package com.example.tteoksang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "id")
    int id;

    @Column(name = "access_token")
    String accessToken;

    @Column(name = "expired_date")
    LocalDateTime expiredDate;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
