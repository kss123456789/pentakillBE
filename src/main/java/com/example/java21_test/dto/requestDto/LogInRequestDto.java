package com.example.java21_test.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LogInRequestDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}