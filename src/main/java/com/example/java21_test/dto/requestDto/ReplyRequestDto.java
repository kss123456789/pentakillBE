package com.example.java21_test.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReplyRequestDto {
    @NotBlank
    String content;
}
