package com.example.java21_test.dto.responseDto;

import com.example.java21_test.entity.PointLog;
import lombok.Getter;

@Getter
public class PointLogResponseDto {
    private String matchId;
    private int pointAmount;
    private String userEmail;
    private String teamCode;
    private String status; // enum 가능

    public PointLogResponseDto(PointLog pointLog) {
        this.matchId = pointLog.getSchedule().getMatchId();
        this.pointAmount = pointLog.getAmount();
        this.userEmail = pointLog.getPoint().getUser().getEmail();
        this.teamCode = pointLog.getTeamCode();
        this.status = pointLog.getStatus();
    }
}
