package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.PredictedStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictedStockHistoryRepository extends JpaRepository<PredictedStockHistory, Integer> {
}
