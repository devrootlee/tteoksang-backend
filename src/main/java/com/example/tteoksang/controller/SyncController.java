package com.example.tteoksang.controller;

import com.example.tteoksang.servcie.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
public class SyncController {
    private final BatchService batchService;

    @PostMapping("/stockKr")
    public String SyncStockKr() {
        batchService.stockKrSync();

        return "sync finish";
    }

    @PostMapping("/stockUs")
    public String SyncStockUs() {
        batchService.stockUsSync();

        return "sync finish";
    }
}
