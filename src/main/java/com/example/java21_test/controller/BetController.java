package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.dto.responseDto.WeeklySchedulesResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.ApiUpdateService;
import com.example.java21_test.service.BetService;
import com.example.java21_test.service.ProbabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/back/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;
    private final ProbabilityService probabilityService;
    private final ApiUpdateService apiUpdateService;

    // 최근 진행중 또는 진행될 토너먼트의 주차별 일정
    @GetMapping("/recentTournament/schedules")
    public ResponseEntity<?> getRecentTournamentSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<WeeklySchedulesResponseDto> responseDto = betService.getRecentTournamentSchedules(userDetails);
        return ResponseEntity.ok()
                .body(responseDto);
    }

    // 최근 토너먼트 저장 // 관리자
    @GetMapping("/saveTournaments")
    public ResponseEntity<?> saveTournaments() {
        betService.saveTournaments();
        return ResponseEntity.ok()
                .body("tournament update");
    }

    // ds 팀에 보낼 요청겸 승률 예측값 받아오기
    @GetMapping("/updateRate")
    public ResponseEntity<?> updateWinRate() {
        probabilityService.saveProbability();
        return ResponseEntity.ok()
                .body("probability update");
    }

    // 오늘 경기일정 등록
    @GetMapping("/scheduler")
    public ResponseEntity<?> addScheduler() {
        apiUpdateService.checkTodaySchedule();
        return ResponseEntity.ok()
                .body("today schedule set");
    }
}
