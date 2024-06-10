package com.example.java21_test.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class BettingScheduleResponseDto {

    private String startTime;
    private String state;
    private String type;
    private String blockName;

    private boolean betting;
    private int amount;
    private String teamCode;
    private String status;

    private League league;
    private MatchRatio match;

    public BettingScheduleResponseDto(String startTime, String state, String type, String blockName,
                                      boolean betting, int amount, String teamCode, String status,
                                     League league, MatchRatio match) {

        this.startTime = startTime;
        this.state = state;
        this.type = type;
        this.blockName = blockName;

        this.betting = betting;
        this.amount = amount;
        this.teamCode = teamCode;
        this.status = status;

        this.league = league;
        this.match = match;
    }
}


@Getter
class MatchRatio {
    private String id;
    private List<String> flags;
    private List<TeamRatio> teams;
    private Strategy strategy;

    public MatchRatio(String id, List<String> flags, List<TeamRatio> teams, Strategy strategy) {
        this.id = id;
        this.flags = flags;
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