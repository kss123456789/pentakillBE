package com.example.java21_test.controller;

import com.example.java21_test.dto.PointBettngRequestDto;
import com.example.java21_test.dto.PointLogResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/bettings")
    public StatusCodeResponseDto<PointLogResponseDto> pointBetting(@RequestBody PointBettngRequestDto pointBettngRequestDto,
                                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return pointService.pointBetting(pointBettngRequestDto, userDetails.getUser());
    }

    @GetMapping("/checkingOdds")
    public StatusCodeResponseDto<Void> checkOdds(String matchId) {
        return pointService.checkOdds(matchId);
    }
}
