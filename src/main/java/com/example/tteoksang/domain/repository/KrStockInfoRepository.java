package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.KrStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KrStockInfoRepository extends JpaRepository<KrStockInfo, String> {

    List<KrStockInfo> findByStockIdContainingIgnoreCaseOrStockNameContainingIgnoreCase(String stockId, String stockName);
}
