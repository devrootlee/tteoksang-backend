package com.example.tteoksang.exception;

import com.example.tteoksang.dto.responsedto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    private final SlackNotification slackNotification;

    // request 오류
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<String>> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.error(e.getMessage(), e);

        return ResponseEntity.status(ValidationCode.REQUEST_ERROR.getCode()).body(CommonResponse.fail(ValidationCode.REQUEST_ERROR.getMsg()));
    }

    //외부 API 오류
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<CommonResponse<String>> RuntimeException(RuntimeException e) {
        LOGGER.error(e.getMessage(), e);

//        slackNotification.sendNotification("500", "서버오류");

        return ResponseEntity.status(501).body(CommonResponse.fail(""));
    }

    //서버 오류
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonResponse<String>> Exception(Exception e) {
        LOGGER.error(e.getMessage(), e);

//        slackNotification.sendNotification("500", "오류");
        return ResponseEntity.status(ValidationCode.SERVER_ERROR.getCode()).body(CommonResponse.fail(ValidationCode.SERVER_ERROR.getMsg()));
    }
}
