package com.example.tteoksang.service;

import com.example.tteoksang.domain.KisToken;
import com.example.tteoksang.domain.repository.KisTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    public List<Map<String, Object>> getKisKrStockPricesLast30Days(String stockId) {
        List<Map<String, Object>> result = new ArrayList<>();

        String kisToken = getKisToken();

        Map response = kisWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/uapi/domestic-stock/v1/quotations/inquire-daily-price")
                        // 조건 시장 분류 코드(J: KRX, NX: NXT, UN: 통합)
                        .queryParam("FID_COND_MRKT_DIV_CODE", "UN")
                        // 종목코드
                        .queryParam("FID_INPUT_ISCD", stockId)
                        // 기간 분류 코드(D: 30일, W: 최근 30주, M: 최근 30개월)
                        .queryParam("FID_PERIOD_DIV_CODE", "D")
                        // 수정주가 원주가 가격(0: 수정주가미반영, 1: 수정주가반영)
                        .queryParam("FID_ORG_ADJ_PRC", "1")
                        .build())
                .header("authorization", "Bearer " + kisToken)
                .header("appkey", kisAppKey)
                .header("appsecret", kisAppSecret)
                .header("tr_id", "FHKST01010400")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && response.get("output") instanceof List) {
            result = (List<Map<String, Object>>) response.get("output");
        }
        return result;
    }

    // KIS 미국 주식 최근 30일 주가
    public List<Map<String, Object>> getKisUsStockPricesLast30Days() {
        List<Map<String, Object>> result = new ArrayList<>();

        return result;
    }
}
