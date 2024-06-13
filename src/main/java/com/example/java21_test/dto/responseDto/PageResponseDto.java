package com.example.java21_test.dto.responseDto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {
    private int statusCode;
    private String message;
    private final List<T> data;
    private int currentPage = 0;
    private int totalPages = 0;
    private long totalElements = 0l;
    private int pageSize = 0;

    public PageResponseDto(int statusCode, String message, Page<T> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data.getContent();
        this.currentPage = data.getNumber();
        this.totalPages = data.getTotalPages();
        this.totalElements = data.getTotalElements();
        this.pageSize = data.getSize();
    }

    public PageResponseDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = null;
    }
}
