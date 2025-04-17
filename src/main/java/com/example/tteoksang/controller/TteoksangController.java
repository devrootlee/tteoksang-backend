package com.example.tteoksang.controller;

import com.example.tteoksang.common.util.CommonUtil;
import com.example.tteoksang.dto.responsedto.CommonResponse;
import com.example.tteoksang.dto.responsedto.GetStockResponseDto;
import com.example.tteoksang.service.TteoksangService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class TteoksangController {
    private final CommonUtil commonUtil;

    private final TteoksangService tteoksangService;

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // 주식 조회
    @GetMapping("/stock")
    public ResponseEntity<CommonResponse<GetStockResponseDto>> getStock(@RequestParam(value = "stockId") String stockId,
                                                                       @RequestParam(value = "stockName") String stockName) {
        return ResponseEntity.ok(CommonResponse.success(tteoksangService.selectStock(stockId, stockName)));
    }

    // 주식 주가 전망 예측
    @GetMapping("/prediction")
    public ResponseEntity getPrediction(HttpServletRequest request,
                                        @RequestParam(value = "nationType") String nationType,
                                        @RequestParam(value = "stockId") String stockId,
                                        @RequestParam(value = "market") String market) {
        String ip = getClientIp(request);
        return commonUtil.ApiResponse(tteoksangService.selectPrediction(nationType, stockId, market, ip));
    }

    // 조회 순위 top10
    @GetMapping("/top10")
    public ResponseEntity getPredictionTop10() {
        return commonUtil.ApiResponse(tteoksangService.selectPredictionTop10());
    }

}
