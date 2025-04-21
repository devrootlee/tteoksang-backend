package com.example.tteoksang.dto.responsedto;

import com.example.tteoksang.exception.ValidationCode;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CommonRes<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> CommonRes<T> success(T data) {
        return new CommonRes<>(true, ValidationCode.SUCCESS.getMsg(), data);
    }

    public static <T> CommonRes<T> fail(String message) {
        return new CommonRes<>(false, message, null);
    }
}
