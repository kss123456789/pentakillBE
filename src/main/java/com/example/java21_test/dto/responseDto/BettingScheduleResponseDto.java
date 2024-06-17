package com.example.java21_test.dto.responseDto;

import lombok.Getter;
import java.util.List;

@Getter
public class BettingScheduleResponseDto {

    private String startTime;
    private String state;
    private String type;
    private String blockName;
    private LeagueScheduleResponseDto.League league;
    private MatchRatio match;

    public BettingScheduleResponseDto(String startTime, String state, String type, String blockName,
                                      LeagueScheduleResponseDto.League league, MatchRatio match) {

        this.startTime = startTime;
        this.state = state;
        this.type = type;
        this.blockName = blockName;
        this.league = league;
        this.match = match;
    }

    @Getter
    public static class MatchRatio {
        private String id;
        private List<TeamRatio> teams;
        private LeagueScheduleResponseDto.Strategy strategy;

        private boolean betting;
        private int amount;
        private String teamCode;
        private String status;

        public MatchRatio(String id, boolean betting, int amount, String teamCode, String status,
                          List<TeamRatio> teams, LeagueScheduleResponseDto.Strategy strategy) {
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
    public static class TeamRatio extends LeagueScheduleResponseDto.Team {
        private float ratio;
        private float probability;
        public TeamRatio(String name, String code, String image, LeagueScheduleResponseDto.Result result, LeagueScheduleResponseDto.Record record,
                         float ratio, float probability) {
            super(name, code, image, result, record);
            this.ratio = ratio;
            this.probability = probability;
        }
    }

}