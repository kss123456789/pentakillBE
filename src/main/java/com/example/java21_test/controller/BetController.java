package com.example.java21_test.controller;

import com.example.java21_test.dto.requestDto.ProbabilityRequestDto;
import com.example.java21_test.dto.responseDto.RecentWeeklySchedulesResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.BetService;
import com.example.java21_test.service.ProbabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final ProbabilityService probabilityService;

    // 최근 토너먼트 저장 // 관리자
    @GetMapping("/saveTournaments")
    public StatusCodeResponseDto<Void> saveTournaments() {
        betService.saveTournaments();
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "tournament update");
    }

    // 최근 진행중 또는 진행될 토너먼트의 주차별 일정
    @GetMapping("/recentTournament/schedules")
    public StatusCodeResponseDto<RecentWeeklySchedulesResponseDto> getRecentTournamentSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return betService.getRecentTournamentSchedules(userDetails);
    }

    // ds 팀에 보낼 요청겸 승률 예측값 받아오기
    @GetMapping("/updateRate")
    public StatusCodeResponseDto<ProbabilityRequestDto> updateWinRate(String matchId) {
        return probabilityService.saveProbability(matchId);
    }
}
