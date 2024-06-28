package com.example.java21_test.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class WeeklySchedulesPageResponseDto {
    private List<LeagueScheduleResponseDto> weeklySchedules;
    private List<String> blockKeySet;
    private int currentBlockNameIndex;
    private int currentPage;
    private int totalPages;

    public WeeklySchedulesPageResponseDto(List weeklySchedules, List blockKeySet, int currentBlockNameIndex,
                                          int currentPage, int totalPages) {
        this.weeklySchedules = weeklySchedules;
        this.blockKeySet = blockKeySet;
        this.currentBlockNameIndex = currentBlockNameIndex;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
