package com.example.java21_test.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GoogleLoginRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String googleAccessToken;
}
