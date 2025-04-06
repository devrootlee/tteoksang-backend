package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    //주식 검색
    List<Stock> findByStockIdContainingIgnoreCaseOrStockNameContainingIgnoreCase(String stockId, String stockName);
}
