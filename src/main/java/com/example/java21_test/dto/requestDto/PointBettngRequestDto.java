package com.example.java21_test.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PointBettngRequestDto {
    @NotBlank
    private String matchId;
    @NotNull
    private int point;
    @NotBlank
    private String teamCode;
}
