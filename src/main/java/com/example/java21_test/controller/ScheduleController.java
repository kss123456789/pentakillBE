package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.PageResponseScheduleByDateDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/back/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 전체 경기 일정
    // requestDto를 만족하지않았을때 에러 뜨게 하기!!
    @GetMapping("/leagues")
    public ResponseEntity<?> getLeagueSchedules(@RequestParam Integer size, @RequestParam Integer page,
                                                Integer year, Integer month) {
        PageResponseScheduleByDateDto<LocalDate, List<Schedule>> pageResponseDto =
                scheduleService.getLeagueSchedules(size, page, year, month);
        return ResponseEntity.ok()
                .body(pageResponseDto);
    }

    // 전체 일정 업데이트(오늘날짜의 기록이 있을시 스케줄 등록) // 관리자
    @GetMapping("/update")
    public ResponseEntity<?> updateSchedule() {
        scheduleService.saveLeagueSchedules();
        return ResponseEntity.ok()
                .body("fin");
    }
    // 리그별 세부 경기 일정 // 안하게 될지도 모름 우선도 낮음
}
