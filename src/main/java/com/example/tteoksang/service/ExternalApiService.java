package com.example.tteoksang.service;

import com.example.tteoksang.domain.KisToken;
import com.example.tteoksang.domain.repository.KisTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    @Value("${external.api.crawler}")
    private String crawler;

    @Value("${external.api.kis}")
    private String kis;
    @Value("${kis.appkey}")
    private String kisAppKey;
    @Value("${kis.appsecret}")
    private String kisAppSecret;

    private WebClient crawlerWebClient;
    private WebClient kisWebClient;

    private final KisTokenRepository kisTokenRepository;

    @PostConstruct
    public void init() {
        this.crawlerWebClient = WebClient.builder()
                .baseUrl(crawler)
                .build();
        this.kisWebClient = WebClient.builder()
                .baseUrl(kis)
                .build();
    }

    // 한국 주식 수동 동기화
    public void StockKrSync() {
        crawlerWebClient.post()
                .uri("/update-stock-kr")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // 미국 주식 수동 동기화
    public void StockUsSync() {
        crawlerWebClient.post()
                .uri("/update-stock-us")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // KIS 토큰 가져오기
    public String getKisToken() {
        String result = "";

        // db 토큰 존재 확인
        Optional<KisToken> selectKisToken = kisTokenRepository.findById(1);

        // 유효기간 parser
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 토큰이 없거나 만료된 경우 갱신
        if (selectKisToken.isEmpty() || selectKisToken.get().getExpiredDate().isBefore(LocalDateTime.now())) {
            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("grant_type", "client_credentials");
            requestBodyMap.put("appkey", kisAppKey);
            requestBodyMap.put("appsecret", kisAppSecret);

            Map response = kisWebClient.post()
                    .uri("/oauth2/tokenP")
                    .bodyValue(requestBodyMap)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            KisToken kisToken = KisToken.builder()
                    .id(1) // ID를 명시하지 않으면 insert로 동작
                    .accessToken(response.get("access_token").toString())
                    .expiredDate(LocalDateTime.parse(response.get("access_token_token_expired").toString(), formatter))
                    .createdAt(LocalDateTime.now())
                    .build();

            kisTokenRepository.save(kisToken);
            result = kisToken.getAccessToken();
        } else {
            result = selectKisToken.get().getAccessToken();
        }

        return result;
    }

    // KIS 한국 주식 최근 30일 주가
    public List<Map<String, Object>> getKisKrStockPricesLast100Days(String kisToken, String stockId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 현재 서버 시간 기준 날짜 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String endDate = LocalDate.now().format(formatter); // 오늘 날짜 (종료일)
        String startDate = LocalDate.now().minusDays(200).format(formatter); // 보정값 200일 100일 전 (시작일)

        Map response = kisWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice")
                        // 조건 시장 분류 코드(J: KRX, NX: NXT, UN: 통합)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "UN")
                        // 종목코드
                        .queryParam("FID_INPUT_ISCD", stockId)
                        // 조회 시작날짜
                        .queryParam("FID_INPUT_DATE_1", startDate)
                        // 조회 종료날짜
                        .queryParam("FID_INPUT_DATE_2", endDate)
                        // 기간 분류 코드(D: 30일, W: 최근 30주, M: 최근 30개월)
                        .queryParam("FID_PERIOD_DIV_CODE", "D")
                        // 수정주가 원주가 가격(0: 수정주가미반영, 1: 수정주가반영)
                        .queryParam("FID_ORG_ADJ_PRC", "1")
                        .build())
                .header("authorization", "Bearer " + kisToken)
                .header("appkey", kisAppKey)
                .header("appsecret", kisAppSecret)
                .header("tr_id", "FHKST03010100")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.get("output2") instanceof List) {
            result = (List<Map<String, Object>>) response.get("output2");
            System.out.println(startDate);
            System.out.println(endDate);
            System.out.println(result);
            System.out.println(result.size());
        }
        return result;
    }

    // KIS 미국 주식 최근 100일 주가
    public List<Map<String, Object>> getKisUsStockPricesLast100Days(String kisToken, String stockId, String market) {
        List<Map<String, Object>> result = new ArrayList<>();

        // KIS의 마켓 코드와 DB의 마켓코드가 다르기 때문에 KIS에 맞게 변경
        String convertMarket = switch (market) {
            case "NASDAQ" -> "NAS";
            case "NYSE" -> "NYS";
            case "AMEX" -> "AMS";
            default -> throw new IllegalArgumentException("지원하지 않는 마켓 코드: " + market);
        };

        Map response = kisWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/overseas-price/v1/quotations/dailyprice")
                        // 거래소 코드 (NYS : 뉴욕, NAS : 나스닥, AMS : 아멕스)
                        .queryParam("EXCD",convertMarket)
                        // 종목코드
                        .queryParam("SYMB", stockId)
                        // 일/주/월 구분(0 : 일, 1 : 주, 2 : 월)
                        .queryParam("GUBN", 0)
                        // 조회기준일자(YYYYMMDD) ※ 공란 설정 시, 기준일 오늘 날짜로 설정
                        .queryParam("BYMD", "")
                        // 수정주가반영 여부(0 : 미반영, 1 : 반영)
                        .queryParam("MODP", 1)
                        .build())
                .header("authorization", "Bearer " + kisToken)
                .header("appkey", kisAppKey)
                .header("appsecret", kisAppSecret)
                .header("tr_id", "HHDFS76240000")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.get("output2") instanceof List) {
            result = (List<Map<String, Object>>) response.get("output2");
        }
        return result;
    }
}
