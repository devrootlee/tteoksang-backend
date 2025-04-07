package com.example.tteoksang.service;

import com.example.tteoksang.domain.Stock;
import com.example.tteoksang.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Map<String, Object> selectPrediction(String nationType, String stockId, String market) {
        Map<String, Object> result = new HashMap<>();
        result.put("nationType", nationType);

        String kisToken = externalApiService.getKisToken();

        List<Map<String, Object>> stockPriceList = new ArrayList<>();

        if (nationType.equals("한국")) {
            //한국 주식 100거래일 정보 조회
            List<Map<String, Object>> getKisKrStockPricesLast90Days = externalApiService.getKisKrStockPricesLast100Days(kisToken, stockId);
            // 90거래일 미만이면 예측 불가
            if (getKisKrStockPricesLast90Days.size() < 90) {
                result.put("chart", null);
                return result;
            }
            // 90거래일 데이터만 필요
            getKisKrStockPricesLast90Days = getKisKrStockPricesLast90Days.subList(0 ,90);
            for (Map<String, Object> item : getKisKrStockPricesLast90Days) {
                Map<String, Object> stockPriceMap = new HashMap<>();
                stockPriceMap.put("date", item.get("stck_bsop_date"));
                stockPriceMap.put("closePrice", item.get("stck_clpr"));

                stockPriceList.add(stockPriceMap);
            }
        } else {
            // 미국 주식 100거래일 정보 조회
            List<Map<String, Object>> getKisUsStockPricesLast100Days = externalApiService.getKisUsStockPricesLast100Days(kisToken, stockId, market);
            // 90거래일 미만이면 예측 불가
            if (getKisUsStockPricesLast100Days.size() < 90) {
                result.put("chart", null);
                return result;
            }
            // 90거래일 데이터만 필요
            getKisUsStockPricesLast100Days = getKisUsStockPricesLast100Days.subList(0 ,90);
            for (Map<String, Object> item : getKisUsStockPricesLast100Days) {
                Map<String, Object> stockPriceMap = new HashMap<>();
                stockPriceMap.put("date", item.get("xymd"));
                stockPriceMap.put("closePrice", item.get("clos"));

                stockPriceList.add(stockPriceMap);
            }
        }

        // ----- 공통 예측 로직 시작 -----
        int n = 90;
        List<Double> closePrices = stockPriceList.stream()
                .map(item -> Double.parseDouble(item.get("closePrice").toString()))
                .toList();

        // SMA
        double sma = closePrices.stream().mapToDouble(Double::doubleValue).sum() / n;

        // EMA
        List<Double> emaList = new ArrayList<>();
        double multiplier = 2.0 / (n + 1);
        double prevEma = sma;
        for (int i = closePrices.size() - 1; i >= 0; i--) {
            double closePrice = closePrices.get(i);
            double ema = (closePrice - prevEma) * multiplier + prevEma;
            emaList.add(0, ema);
            prevEma = ema;
        }

        // 이상치 제거 후 선형 회귀 계산
        List<Double> sortedPrices = new ArrayList<>(closePrices);
        Collections.sort(sortedPrices);
        int trim = (int)(n * 0.05); // 상하위 5% 제거
        List<Double> trimmedPrices = sortedPrices.subList(trim, n - trim);

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < trimmedPrices.size(); i++) {
            double y = trimmedPrices.get(i);
            int x = trimmedPrices.size() - i;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope = (trimmedPrices.size() * sumXY - sumX * sumY) / (trimmedPrices.size() * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / trimmedPrices.size();
        List<Double> regressionList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int x = n - i;
            double y = slope * x + intercept;
            regressionList.add(y);
        }

        // 추세 판단
        double averagePrice = closePrices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double stddev = calculateStandardDeviation(closePrices, averagePrice);
        double threshold = stddev * 0.1; // 표준편차의 20% 기준

        String trend = calculateTrend(slope, averagePrice, threshold);
        result.put("trend", trend);

        // 결과 생성
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < stockPriceList.size(); i++) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", stockPriceList.get(i).get("date"));
            resultMap.put("closePrice", closePrices.get(i));
            resultMap.put("sma", Math.round(sma * 100.0) / 100.0);
            resultMap.put("ema", Math.round(emaList.get(i) * 100.0) / 100.0);
            resultMap.put("linear", Math.round(regressionList.get(i) * 100.0) / 100.0);
            resultList.add(resultMap);
        }

        result.put("chart", resultList);
        return result;
    }

    private String calculateTrend(double slope, double averagePrice, double threshold) {
        double ratio = slope / averagePrice;
        double dynamicThreshold = threshold / averagePrice;
        if (ratio > dynamicThreshold) return "상승 예상";
        if (ratio < -dynamicThreshold) return "하락 예상";
        return "보합 예상";
    }

    private double calculateStandardDeviation(List<Double> prices, double average) {
        double variance = prices.stream()
                .mapToDouble(p -> Math.pow(p - average, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }
}
