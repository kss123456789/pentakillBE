package com.example.java21_test.controller;

import com.example.java21_test.dto.PageResponseDto;
import com.example.java21_test.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 전체 경기 일정
    // requestDto를 만족하지않았을때 에러 뜨게 하기!!
    @GetMapping("/saveleagues")
    public PageResponseDto<?> saveLeagueSchedules() {
        return scheduleService.saveLeagueSchedules();
    }

    @GetMapping("/leagues")
    public PageResponseDto<?> getLeagueSchedules(Integer size, Integer page) {
        return scheduleService.getLeagueSchedules(size, page);
    }

    // 리그별 세부 경기 일정 // 안하게 될지도 모름 우선도 낮음

}
