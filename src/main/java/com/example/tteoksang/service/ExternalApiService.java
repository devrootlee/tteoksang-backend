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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public void stockKrSync() {
        crawlerWebClient.post()
                .uri("/update-stock-kr")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // 미국 주식 수동 동기화
    public void stockUsSync() {
        crawlerWebClient.post()
                .uri("/update-stock-us")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    // KIS 토큰 가져오기
    public Map<String, Object> getKisToken() {
        Map<String, Object> result = new HashMap<>();

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
            result.put("kisToken", kisToken.getAccessToken());
        } else {
            result.put("kisToken", selectKisToken.get().getAccessToken());
        }

        return result;
    }
}
