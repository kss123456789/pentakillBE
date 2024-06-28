package com.example.java21_test.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class WeeklySchedulesResponseDto {
    private List<List<LeagueScheduleResponseDto>> weeklySchedules;
    private List<String> blockKeySet;
    private int currentWeek;
    private int totalWeek;
    private float accuracy;

    public WeeklySchedulesResponseDto(List weeklySchedules, List blockKeySet, int currentWeek, int totalWeek, float accuracy) {
        this.weeklySchedules = weeklySchedules;
        this.blockKeySet = blockKeySet;
        this.currentWeek = currentWeek;
        this.totalWeek = totalWeek;
        this.accuracy = accuracy;
    }
}
