package com.example.java21_test.dto;

import lombok.Getter;

@Getter
public class StatusCodeResponseDto<T> {
    private int statusCode;
    private String message;
    private final T data;

    public StatusCodeResponseDto(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
