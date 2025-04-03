package com.example.tteoksang.controller;

import com.example.tteoksang.servcie.TteoksangService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TteoksangController {
    private final TteoksangService tteoksangService;


}
