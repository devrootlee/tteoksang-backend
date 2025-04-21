package com.example.tteoksang.dto.responsedto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginStatusRes {
    private boolean isLoggedIn;
    private String nickname;
}
