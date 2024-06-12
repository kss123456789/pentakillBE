package com.example.java21_test.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class BettingScheduleResponseDto {

    private String startTime;
    private String state;
    private String type;
    private String blockName;
    private League league;
    private MatchRatio match;

    public BettingScheduleResponseDto(String startTime, String state, String type, String blockName,
                                     League league, MatchRatio match) {

        this.startTime = startTime;
        this.state = state;
        this.type = type;
        this.blockName = blockName;
        this.league = league;
        this.match = match;
    }
}


@Getter
class MatchRatio {
    private String id;
    private List<TeamRatio> teams;
    private Strategy strategy;

    private boolean betting;
    private int amount;
    private String teamCode;
    private String status;

    public MatchRatio(String id, boolean betting, int amount, String teamCode, String status, List<TeamRatio> teams, Strategy strategy) {
        this.id = id;
        this.betting = betting;
        this.amount = amount;
        this.teamCode = teamCode;
        this.status = status;
        this.teams = teams;
        this.strategy = strategy;
    }
}


@Getter
class TeamRatio extends Team {
    private float ratio;
    public TeamRatio(String name, String code, String image, Result result, Record record, float ratio) {
        super(name, code, image, result, record);
        this.ratio =ratio;
    }
}