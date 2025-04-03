package com.example.tteoksang.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock", indexes = {
        @Index(name = "idx_exchange_eng", columnList = "exchange_eng"),
        @Index(name = "idx_exchange_kor", columnList = "exchange_kor"),
        @Index(name = "idx_stock_name_eng", columnList = "stock_name_eng"),
        @Index(name = "idx_stock_name_kor", columnList = "stock_name_kor")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "nation_type", nullable = false)
    private String nationType;

    @Column(name = "exchange_eng", nullable = false)
    private String exchangeEng;

    @Column(name = "exchange_kor", nullable = false)
    private String exchangeKor;

    @Column(name = "stock_name_eng")
    private String stockNameEng;

    @Column(name = "stock_name_kor", nullable = false)
    private String stockNameKor;

    @Column(name = "market_value", nullable = false)
    private Long marketValue;

    @Column(name = "market_value_usd")
    private String marketValueUsd;

    @Column(name = "market_value_kor", nullable = false)
    private String marketValueKor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //엔티티가 저장되기 전에 createdAt 자동 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    //엔티티가 변경되면 자동 업데이트
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
