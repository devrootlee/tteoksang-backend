package com.example.tteoksang.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalSignUpReq {

    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^[a-z][a-zA-Z0-9]{4,19}$",
            message = "아이디는 소문자로 시작하며 영문과 숫자로 5~20자여야 합니다."
    )
    private String localId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
    )
    private String localPassword;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]{2,15}$",
            message = "닉네임은 특수문자를 제외한 2~15자여야 합니다."
    )
    private String nickname;
}
