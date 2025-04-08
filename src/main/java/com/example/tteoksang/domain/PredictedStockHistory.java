package com.example.tteoksang.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "predicted_stock_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictedStockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "date_key", nullable = false)
    int dateKey;

    @Column(name = "stock_id", nullable = false)
    String stockId;

    @Column(name = "ip_address", nullable = false)
    String ipAddress;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
