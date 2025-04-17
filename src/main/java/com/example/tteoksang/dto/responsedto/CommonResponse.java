package com.example.tteoksang.dto.responsedto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, "요청에 성공했습니다", data);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(false, message, null);
    }
}
