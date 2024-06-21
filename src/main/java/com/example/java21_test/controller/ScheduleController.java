package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.PageResponseScheduleByDateDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 전체 경기 일정
    // requestDto를 만족하지않았을때 에러 뜨게 하기!!
    //page, size 값이 안 들어왔을 때 NullpointerException이 나온다아아 이것도 에러처리해보시지
    @GetMapping("/leagues")
    public PageResponseScheduleByDateDto<LocalDate, List<Schedule>> getLeagueSchedules(Integer size, Integer page,
                                                                                       Integer year, Integer month) {
        return scheduleService.getLeagueSchedules(size, page, year, month);
    }

    // 전체 일정 업데이트(오늘날짜의 기록이 있을시 스케줄 등록) // 관리자
    @GetMapping("/update")
    public StatusCodeResponseDto<Void> updateSchedule() {
        scheduleService.saveLeagueSchedules();
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "완");
    }

    // 실시간 10분 간격 업데이트 (현재 진행중인 경기가 있는데 서버실행시간이 꼬인다던가했을때 사용) // 관리자
    // 잠시만... 위에 전체 일정 업데이트만 해도 루프가 돌아가던가...?

    // 리그별 세부 경기 일정 // 안하게 될지도 모름 우선도 낮음

}
