package com.example.java21_test.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class RecentWeeklySchedulesResponseDto {
    private List<List<LeagueScheduleResponseDto>> weeklySchedules;
    private List<String> blockKeySet;
    private int currentWeek;
    private int totalWeek;

    public RecentWeeklySchedulesResponseDto(List weeklySchedules, List blockKeySet, int currentWeek, int totalWeek) {
        this.weeklySchedules = weeklySchedules;
        this.blockKeySet = blockKeySet;
        this.currentWeek = currentWeek;
        this.totalWeek = totalWeek;
    }
}
