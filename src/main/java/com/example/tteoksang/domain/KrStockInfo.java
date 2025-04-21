package com.example.tteoksang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jfr.Description;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "kr_stock_info")
@Entity
@Description(value = "한국 주식 정보")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KrStockInfo {
    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "market")
    private String market;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
