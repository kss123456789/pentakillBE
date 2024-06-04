package com.example.java21_test.controller;

import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.service.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    // 최근 토너먼트 저장
    @GetMapping("/saveTournaments")
    public StatusCodeResponseDto<?> saveTournaments() {
        return betService.saveTournaments();
    }

    // 최근 진행중 또는 진행될 토너먼트의 주차별 일정
    @GetMapping("/recentTournament/schedules")
    public StatusCodeResponseDto<?> getRecentTournamentSchedules() {
        return betService.getRecentTournamentSchedules();
    }

    // 특정 schedule에 point 걸기

    // 특정 schedule에 걸린 포인트 분배

    // 특정

}
