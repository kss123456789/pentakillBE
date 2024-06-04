package com.example.java21_test.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RecentWeeklySchedulesResponseDto {
    private List<List<LeagueScheduleResponseDto>> weeklySchedules;
    private int currentWeek;
    private int totalWeek;

    public RecentWeeklySchedulesResponseDto(List weeklySchedules, int currentWeek, int totalWeek) {
        this.weeklySchedules = weeklySchedules;
        this.currentWeek = currentWeek;
        this.totalWeek = totalWeek;
    }
}
