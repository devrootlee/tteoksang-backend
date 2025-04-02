package com.example.tteoksang.servcie;

import com.example.tteoksang.domain.Stock;
import com.example.tteoksang.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final StockRepository stockRepository;

    //한국 주식 동기화
    @Transactional
    public void stockKrSync() {
        WebClient webClient = WebClient.create("https://m.stock.naver.com");

        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/marketValue/all")
                        .build()

                ).exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                       return clientResponse.bodyToMono(Map.class);
                    } else throw new RuntimeException();
                }).block();


        if (response != null) {
            //page 개수 구하기
            int pageSize = Integer.parseInt(response.get("pageSize").toString());
            int totalCount = Integer.parseInt(response.get("totalCount").toString());
            int totalPage = (int) Math.ceil((double) totalCount / pageSize);

            //전체 page 개수만큼 호출
            for (int i = 0; i < totalPage; i++) {
                int requestPage = i + 1;

                response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/stocks/marketValue/all")
                                .queryParam("page", requestPage)
                                .build()
                        ).exchangeToMono(clientResponse -> {
                            if (clientResponse.statusCode().is2xxSuccessful()) {
                                return clientResponse.bodyToMono(Map.class);
                            } else return null;
                        }).block();
                System.out.println(response);
                List<Map> apiStocks = (List) response.get("stocks");
                if (!apiStocks.isEmpty()){
                    List<Stock> saveStocks = new ArrayList<>();
                    for (Map apiItem : apiStocks) {
                        //주식만 저장(etf,etn 제외)
                        if (apiItem.get("stockEndType").equals("stock") && apiItem.get("marketValue") != null) {
                            Stock stock = Stock.builder()
                                    .stockId(apiItem.get("itemCode").toString())
                                    .nationType(((Map)apiItem.get("stockExchangeType")).get("nationType").toString())
                                    .exchangeEng(((Map)apiItem.get("stockExchangeType")).get("nameEng").toString())
                                    .exchangeKor(((Map)apiItem.get("stockExchangeType")).get("nameKor").toString())
                                    .stockNameEng(null)
                                    .stockNameKor(apiItem.get("stockName").toString())
                                    .marketValue(Integer.parseInt(apiItem.get("marketValue").toString().replace(",","")))
                                    .marketValueUsd(null)
                                    .marketValueKor(apiItem.get("marketValueHangeul").toString())
                                    .build();

                            saveStocks.add(stock);
                        }
                    }
                    stockRepository.saveAll(saveStocks);
                }
            }
        } else {
            // 응답 데이터 없음
        }
    }

    //미국 주식 동기화
    public void stockUsSync() {

    }
}
