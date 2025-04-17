package com.example.tteoksang.domain.repository;

import com.example.tteoksang.domain.KrxStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KrxStockInfoRepository extends JpaRepository<KrxStockInfo, String> {

    List<KrxStockInfo> findByStockIdContainingIgnoreCaseOrStockNameContainingIgnoreCase(String stockId, String stockName);
}
