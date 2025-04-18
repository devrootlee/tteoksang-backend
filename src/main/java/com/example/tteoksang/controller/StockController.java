package com.example.tteoksang.controller;

import com.example.tteoksang.common.util.CommonUtil;
import com.example.tteoksang.dto.requestdto.StockSearchReq;
import com.example.tteoksang.dto.responsedto.CommonRes;
import com.example.tteoksang.dto.responsedto.StockSearchRes;
import com.example.tteoksang.service.StockService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final CommonUtil commonUtil;

    private final StockService stockService;

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // 주식 조회
    @GetMapping("/search")
    public ResponseEntity<CommonRes<StockSearchRes>> stockSearch(@Valid StockSearchReq request) {
        return ResponseEntity.ok(CommonRes.success(stockService.selectStockSearch(request)));
    }

    // 주식 주가 전망 예측
    @GetMapping("/prediction")
    public ResponseEntity stockPrediction(HttpServletRequest request,
                                          @RequestParam(value = "nationType") String nationType,
                                          @RequestParam(value = "stockId") String stockId,
                                          @RequestParam(value = "market") String market) {
        String ip = getClientIp(request);
        return commonUtil.ApiResponse(stockService.selectStockPrediction(nationType, stockId, market, ip));
    }

    // 조회 순위 top10
    @GetMapping("/predictionTop10")
    public ResponseEntity stockPredictionTop10() {
        return commonUtil.ApiResponse(stockService.selectStockPredictionTop10());
    }

}
