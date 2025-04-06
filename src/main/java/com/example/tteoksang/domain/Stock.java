package com.example.tteoksang.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "nation_type", nullable = false)
    private String nationType;

    @Column(name = "market", nullable = false)
    private String market;

    @Column(name = "stock_name", nullable = false)
    private String stockName;
}
