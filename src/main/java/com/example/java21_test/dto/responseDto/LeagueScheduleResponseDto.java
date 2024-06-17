package com.example.java21_test.dto.responseDto;

import lombok.Getter;

import java.util.List;

@Getter
public class LeagueScheduleResponseDto {
    private String startTime;
    private String state;
    private String type;
    private String blockName;
    private League league;
    private Match match;

    public LeagueScheduleResponseDto(String startTime, String state, String type, String blockName,
                                     League league, Match match) {
        this.startTime = startTime;
        this.state = state;
        this.type = type;
        this.blockName = blockName;
        this.league = league;
        this.match = match;
    }


    @Getter
    public static class League {
        private String name;
        private String slug;

        public League(String name, String slug) {
            this.name = name;
            this.slug = slug;
        }
    }

    @Getter
    public static class Match {
        private String id;
        private List<String> flags;
        private List<Team> teams;
        private Strategy strategy;

        public Match(String id, List<String> flags, List<Team> teams, Strategy strategy) {
            this.id = id;
            this.flags = flags;
            this.teams = teams;
            this.strategy = strategy;
        }
    }

    @Getter
    public static class Team {
        private String name;
        private String code;
        private String image;
        private Result result;
        private Record record;

        public Team(String name, String code, String image, Result result, Record record) {
            this.name = name;
            this.code = code;
            this.image = image;
            this.result = result;
            this.record = record;
        }
    }

    @Getter
    public static class Result {
        private String outcome;
        private int gameWins;

        public Result(String outcome, int gameWins) {
            this.outcome = outcome;
            this.gameWins = gameWins;
        }
    }

    @Getter
    public static class Record {
        private int wins;
        private int losses;

        public Record(int wins, int losses) {
            this.wins = wins;
            this.losses = losses;
        }
    }

    @Getter
    public static class Strategy {
        private String type;
        private int count;

        public Strategy(String type, int count) {
            this.type = type;
            this.count = count;
        }
    }

}