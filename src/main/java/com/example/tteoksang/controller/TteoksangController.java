package com.example.tteoksang.controller;

import com.example.tteoksang.service.ExternalApiService;
import com.example.tteoksang.service.TteoksangService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class TteoksangController {
    private final TteoksangService tteoksangService;
    private final ExternalApiService externalApiService;

    @GetMapping("/test")
    public Map test() {
        return externalApiService.getKisToken();
    }
}
