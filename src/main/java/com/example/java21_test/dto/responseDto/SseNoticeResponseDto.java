package com.example.java21_test.dto.responseDto;

import com.example.java21_test.entity.SseNotice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class SseNoticeResponseDto {
    private LocalDateTime time;
    private String type;
    private String team1;
    private String team2;
    private int point = 0;
    private String outcome = "";

    public SseNoticeResponseDto(LocalDateTime time, String type, String team1, String team2) {
        this.time = time;
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
    }

    public SseNoticeResponseDto(LocalDateTime time, String type, String team1, String team2, int point, String outcome) {
        this.time = time;
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
        this.point = point;
        this.outcome = outcome;
    }

    public SseNoticeResponseDto(SseNotice sseNotice) {
        this.time = sseNotice.getCreatedAt();
        this.type = sseNotice.getType();
        this.team1 = sseNotice.getTeam1();
        this.team2 = sseNotice.getTeam2();
        this.point = sseNotice.getPoint();
        this.outcome = sseNotice.getOutcome();
    }
}
