package com.example.java21_test.dto.responseDto;


import lombok.Getter;

import java.util.Map;

@Getter
public class PageResponseScheduleByDateDto<K, V> {
    private int statusCode;
    private String message;
    private Map<K, V> data;
    private int year;
    private int month;
    private int currentPage = 0;
    private int totalPages = 0;
    private long totalElements = 0L;
    private int pageSize = 0;

    public PageResponseScheduleByDateDto(int statusCode, String message, Map<K, V> data, int year, int month,
                                         int currentPage, int totalPages, long totalElements, int pageSize) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.year = year;
        this.month = month;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public PageResponseScheduleByDateDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = null;
    }
}
