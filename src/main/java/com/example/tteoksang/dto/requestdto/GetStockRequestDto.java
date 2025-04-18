package com.example.tteoksang.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStockRequestDto {
    @NotBlank
    private String keyword;
}
