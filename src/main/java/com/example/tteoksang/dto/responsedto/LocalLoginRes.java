package com.example.tteoksang.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalLoginRes {
    private boolean success;

    @JsonIgnore // 바디에 안 보이게 처리
    private String jwt;

}
