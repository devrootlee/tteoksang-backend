package com.example.tteoksang.controller;

import com.example.tteoksang.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {
    private final ExternalApiService externalApiService;

    @PostMapping("/stockKr")
    public String SyncStockKr() {
        externalApiService.StockKrSync();

        return "sync finish";
    }
}
