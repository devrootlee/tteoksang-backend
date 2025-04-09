package com.example.tteoksang.dto.querydto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Top10PredictionStockDto {
    private String stockId;
    private String stockName;
    private String market;
    private Long count;
}
