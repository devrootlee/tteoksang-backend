package com.example.tteoksang.dto.responsedto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateNicknameRes {
    private boolean isPossible;
}
