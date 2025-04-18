package com.example.tteoksang.exception;

public enum ValidationCode {
    SUCCESS(200, "성공"),
    REQUEST_ERROR(400, "사용자 오류"),
    AUTH_ERROR(401, "인증 오류"),
    SERVER_ERROR(500, " 서버 오류");

    private final int code;
    private final String msg;

    ValidationCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
