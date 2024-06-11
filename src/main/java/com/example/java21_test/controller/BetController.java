package com.example.java21_test.controller;

import com.example.java21_test.dto.RecentWeeklySchedulesResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.BetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    // 최근 토너먼트 저장
    @GetMapping("/saveTournaments")
    public StatusCodeResponseDto<Void> saveTournaments() {
        return betService.saveTournaments();
    }

    // 최근 진행중 또는 진행될 토너먼트의 주차별 일정
    @GetMapping("/recentTournament/schedules")
    public StatusCodeResponseDto<RecentWeeklySchedulesResponseDto> getRecentTournamentSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return betService.getRecentTournamentSchedules(userDetails);
    }

    // 특정 schedule에 point 걸기

    // 특정 schedule에 걸린 포인트 분배

    // 특정

}
