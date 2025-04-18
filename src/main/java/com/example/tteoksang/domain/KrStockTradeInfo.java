package com.example.tteoksang.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kr_stock_trade_info")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class KrStockTradeInfo {
    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "close_price")
    private int closePrice;

    @Column(name = "change_rate")
    private double changeRate;

    @Column(name = "open_price")
    private int openPrice;

    @Column(name = "high_price")
    private int highPrice;

    @Column(name = "low_price")
    private int lowPrice;

    @Column(name = "volume")
    private int volume;

    @Column(name = "amount")
    private long amount;

    @Column(name = "capacity")
    private long capacity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
