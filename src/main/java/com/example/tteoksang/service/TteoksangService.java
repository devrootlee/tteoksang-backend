package com.example.tteoksang.service;

import com.example.tteoksang.common.util.CommonUtil;
import com.example.tteoksang.domain.PredictedStockHistory;
import com.example.tteoksang.domain.Stock;
import com.example.tteoksang.domain.repository.PredictedStockHistoryRepository;
import com.example.tteoksang.domain.repository.StockRepository;
import com.example.tteoksang.dto.querydto.Top10PredictionStockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TteoksangService {
    private final ExternalApiService externalApiService;

    private final StockRepository stockRepository;
    private final PredictedStockHistoryRepository predictedStockHistoryRepository;

    private final CommonUtil commonUtil;

    public Map<String, Object> selectStock(String stockId, String stockName) {
        Map<String, Object> result = new HashMap<>();
        List<Stock> stockList = stockRepository.findByStockIdContainingIgnoreCaseOrStockNameContainingIgnoreCase(stockId, stockName);

        result.put("stockList", stockList);

        return result;
    }

    public Map<String, Object> selectPrediction(String nationType, String stockId, String market, String ip) {
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
        int smaPeriod = 20;

        // 종가 리스트 (최신이 0번 인덱스)
        List<Double> closePrices = stockPriceList.stream()
                .map(item -> Double.parseDouble(item.get("closePrice").toString()))
                .toList();

        // SMA (최신 20일 기준)
        List<Double> smaList = new ArrayList<>();
        for (int i = 0; i < closePrices.size(); i++) {
            int end = i + 1;
            int start = Math.max(0, end - smaPeriod);
            smaList.add(closePrices.subList(start, end).stream().mapToDouble(Double::doubleValue).average().orElse(0));
        }
        double lastSma = smaList.get(0); // 최신 SMA

        // EMA (90일 전체 계산)
        List<Double> emaList = new ArrayList<>();
        double multiplier = 2.0 / (smaPeriod + 1);
        double prevEma = smaList.get(smaPeriod - 1); // 초기값은 20일 SMA
        for (int i = smaPeriod - 1; i < closePrices.size(); i++) {
            if (i == smaPeriod - 1) {
                emaList.add(prevEma); // 초기값 추가
            } else {
                double closePrice = closePrices.get(i);
                double ema = (closePrice - prevEma) * multiplier + prevEma;
                emaList.add(ema);
                prevEma = ema;
            }
        }
        // 최신부터 과거로 나머지 EMA 계산
        List<Double> fullEmaList = new ArrayList<>(emaList);
        prevEma = emaList.get(0); // 초기값은 20일째 EMA
        for (int i = smaPeriod - 2; i >= 0; i--) {
            double closePrice = closePrices.get(i);
            double ema = (closePrice - prevEma) * multiplier + prevEma;
            fullEmaList.add(0, ema);
            prevEma = ema;
        }
        double lastEma = fullEmaList.get(0); // 최신 EMA

        // 선형회귀
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < closePrices.size(); i++) {
            double y = closePrices.get(i);
            int x = n - i; // 최신(0번)이 x=90
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        double lrPred = slope * (n + 1) + intercept;

        // 종합 예측
        double predictedPrice = 0.3 * lastSma + 0.4 * lastEma + 0.3 * lrPred;
        double currentPrice = closePrices.get(0);

        // 트렌드 판단
        String trend = calculateTrend(slope, predictedPrice, currentPrice);
        result.put("trend", trend);
        result.put("predictedPrice", Math.round(predictedPrice * 100.0) / 100.0);

        // 차트 데이터
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < closePrices.size(); i++) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("date", stockPriceList.get(i).get("date"));
            resultMap.put("closePrice", closePrices.get(i));
            resultMap.put("sma", Math.round(smaList.get(i) * 100.0) / 100.0);
            resultMap.put("ema", Math.round(fullEmaList.get(i) * 100.0) / 100.0); // 전체 EMA 사용
            resultMap.put("linear", Math.round((slope * (n - i) + intercept) * 100.0) / 100.0);
            resultList.add(resultMap);
        }
        result.put("chart", resultList);

        if (!resultList.isEmpty()) {
            savePredictionToDbAndRedis(ip, stockId, result);
        }

        return result;
    }

    private String calculateTrend(double slope, double predictedPrice, double currentPrice) {
        double change = predictedPrice - currentPrice;
        double threshold = currentPrice * 0.005; // 0.5% 변동
        if (change > threshold || slope > 0) return "상승 예상";
        if (change < -threshold || slope < 0) return "하락 예상";
        return "보합 예상";
    }


    public void savePredictionToDbAndRedis(String ip, String stockId, Map<String, Object> predictionResult) {
        int dateKey = commonUtil.getDateKey();

        // DB 저장
        PredictedStockHistory history = PredictedStockHistory.builder()
                .dateKey(dateKey)
                .stockId(stockId)
                .memberId(ip)
                .createdAt(LocalDateTime.now())
                .build();
        predictedStockHistoryRepository.save(history);

        // Redis 저장
    }

    public Map<String, Object> selectPredictionTop10 () {
       Map<String, Object> result = new HashMap<>();
        // 포매터 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate baseDate = LocalDateTime.now().toLocalDate();

        // 오늘 날짜
        int today = Integer.parseInt(baseDate.format(formatter));

        // 주간 시작일 (월요일)
        int startOfWeek = Integer.parseInt(baseDate.with(DayOfWeek.MONDAY).format(formatter).toString());
        // 주간 종료일 (일요일)
        int endOfWeek = Integer.parseInt(baseDate.with(DayOfWeek.SUNDAY).format(formatter).toString());

        // 월간 시작일
        int startOfMonth = Integer.parseInt(baseDate.withDayOfMonth(1).format(formatter).toString());
        // 월간 종료일
        int endOfMonth = Integer.parseInt(baseDate.withDayOfMonth(baseDate.lengthOfMonth()).format(formatter).toString());

       List<Top10PredictionStockDto> dailyTop10 = predictedStockHistoryRepository.findTop10(today, today);
       List<Top10PredictionStockDto> weeklyTop10 = predictedStockHistoryRepository.findTop10(startOfWeek, endOfWeek);
       List<Top10PredictionStockDto> monthlyTop10 = predictedStockHistoryRepository.findTop10(startOfMonth, endOfMonth);

       result.put("daily", dailyTop10);
       result.put("weekly", weeklyTop10);
       result.put("monthly", monthlyTop10);

       return result;
    }
}
