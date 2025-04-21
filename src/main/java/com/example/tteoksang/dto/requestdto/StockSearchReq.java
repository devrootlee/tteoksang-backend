package com.example.tteoksang.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockSearchReq {
    @NotBlank
    private String keyword;
}
