package com.example.java21_test.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {
    private int statusCode;
    private String message;
    private final List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public PageResponseDto(int statusCode, String message, Page<T> data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data.getContent();
        this.currentPage = data.getNumber();
        this.totalPages = data.getTotalPages();
        this.totalElements = data.getTotalElements();
        this.pageSize = data.getSize();
    }
}
