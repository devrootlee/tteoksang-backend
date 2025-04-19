package com.example.tteoksang.dto.requestdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalLoginReq {
    String localId;

    String localPassword;
}
