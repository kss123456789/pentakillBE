package com.example.java21_test.controller;

import com.example.java21_test.dto.PointBettngRequestDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/bettings")
    public StatusCodeResponseDto<?> pointBetting(@RequestBody PointBettngRequestDto pointBettngRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return pointService.pointBetting(pointBettngRequestDto, userDetails.getUser());
    }

    @GetMapping("/checkingOdds")
    public StatusCodeResponseDto<?> checkOdds(String matchId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return pointService.checkOdds(matchId, userDetails.getUser());
    }
}
