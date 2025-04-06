package com.example.tteoksang.exception;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    private final SlackNotification slackNotification;

    //외부 API 오류
    @ExceptionHandler(value = RuntimeException.class)
    public void RuntimeException(RuntimeException e) {
        LOGGER.error(e.getMessage(), e);

//        slackNotification.sendNotification("500", "서버오류");
    }

    //서버 오류
    @ExceptionHandler(value = Exception.class)
    public void Exception(Exception e) {
        LOGGER.error(e.getMessage(), e);

//        slackNotification.sendNotification("500", "오류");
    }
}
