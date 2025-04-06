package com.example.tteoksang.service;

import com.example.tteoksang.domain.Stock;
import com.example.tteoksang.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TteoksangService {
    private final StockRepository stockRepository;

    private final ExternalApiService externalApiService;

    public Map<String, Object> selectStock(String stockId, String stockName) {
        Map<String, Object> result = new HashMap<>();
        List<Stock> stockList = stockRepository.findByStockIdContainingIgnoreCaseOrStockNameContainingIgnoreCase(stockId, stockName);

        result.put("stockList", stockList);

        return result;
    }
}
