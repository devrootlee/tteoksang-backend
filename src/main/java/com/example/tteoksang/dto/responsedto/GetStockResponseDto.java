package com.example.tteoksang.dto.responsedto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetStockResponseDto {
    private List<Stock> stockList;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stock {
        private String stockId;

        private String market;

        private String stockName;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }
}
