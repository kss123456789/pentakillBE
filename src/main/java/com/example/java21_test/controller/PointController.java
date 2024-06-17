package com.example.java21_test.controller;

import com.example.java21_test.dto.requestDto.PointBettngRequestDto;
import com.example.java21_test.dto.responseDto.PointLogResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PointService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   HttpServletResponse jwtResponse) {
        return pointService.pointBetting(pointBettngRequestDto, userDetails.getUser(), jwtResponse);
    }

    // 포인트 배팅 결과 반영 // 관리자
    @GetMapping("/checkingOdds")
    public StatusCodeResponseDto<Void> checkOdds(String matchId) {
        pointService.checkOdds(matchId);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "완");
    }
}
