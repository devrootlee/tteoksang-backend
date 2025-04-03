package com.example.tteoksang.batch;

import com.example.tteoksang.servcie.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchJob {
    private final BatchService batchService;

    //한국 주식 정보 동기화
    @Scheduled(cron = "0 0 21 * * *")
    public void stockKrJob() {
        batchService.stockKrSync();
    }

    //미국 주식 정보 동기화
    @Scheduled(cron = "0 0 09 * * *")
    public void stockUsJob() {
        batchService.stockUsSync();
    }
}
