package com.example.java21_test.dto.responseDto;

import lombok.Getter;

@Getter
public class AccuracyResponseDto {
    private float accuracy;

    public AccuracyResponseDto(float accuracy) {
        this.accuracy = accuracy;
    }
}
