package com.example.tteoksang.controller;

import com.example.tteoksang.common.util.CommonUtil;
import com.example.tteoksang.service.ExternalApiService;
import com.example.tteoksang.service.TteoksangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class TteoksangController {
    private final CommonUtil commonUtil;

    private final TteoksangService tteoksangService;
    private final ExternalApiService externalApiService;

    @GetMapping("/stock")
    public ResponseEntity getStock(@RequestParam(value = "stockId") String stockId,
                                   @RequestParam(value = "stockName") String stockName) {

        return commonUtil.ApiResponse(tteoksangService.selectStock(stockId, stockName));
    }

    @PostMapping("/predict")
    public ResponseEntity prediction() {
        return commonUtil.ApiResponse(null);
    }
}
