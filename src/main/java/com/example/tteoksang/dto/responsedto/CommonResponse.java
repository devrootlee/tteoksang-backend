package com.example.tteoksang.dto.responsedto;

import com.example.tteoksang.exception.ValidationCode;
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
        return new CommonResponse<>(true, ValidationCode.SUCCESS.getMsg(), data);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(false, message, null);
    }
}
