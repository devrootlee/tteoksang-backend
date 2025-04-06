package com.example.tteoksang.common.util;

import com.example.tteoksang.exception.ValidationCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommonUtil {
    /**
     * ApiResponse util
     * @param data
     * @return
     */
    public ResponseEntity<Map<String, Object>> ApiResponse(Object data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("resultCode", ValidationCode.SUCCESS.getCode());
        body.put("data", data);

        return ResponseEntity.ok(body);
    }
}
