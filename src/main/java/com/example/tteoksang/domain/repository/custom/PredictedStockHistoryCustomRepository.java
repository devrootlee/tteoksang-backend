package com.example.tteoksang.domain.repository.custom;

import com.example.tteoksang.dto.querydto.Top10PredictionStockDto;

import java.util.List;

public interface PredictedStockHistoryCustomRepository {
    List<Top10PredictionStockDto> findTop10(int startDateKey, int endDateKey);
}
