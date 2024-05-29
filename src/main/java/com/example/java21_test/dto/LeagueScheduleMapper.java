package com.example.java21_test.dto;

import com.example.java21_test.entity.Schedule;

import java.util.List;

public class LeagueScheduleMapper {

    public static LeagueScheduleResponseDto toDto(Schedule schedule) {
        League league = new League(schedule.getLeagueName(), schedule.getLeagueSlug());

        Team team1 = new Team(schedule.getTeam1Name(), schedule.getTeam1Code(), schedule.getTeam1Image(),
                new Result(schedule.getTeam1Outcome(), schedule.getTeam1GameWins()),
                new Record(schedule.getTeam1RecordWins(), schedule.getTeam1RecordLosses()));

        Team team2 = new Team(schedule.getTeam2Name(), schedule.getTeam2Code(), schedule.getTeam2Image(),
                new Result(schedule.getTeam2Outcome(), schedule.getTeam2GameWins()),
                new Record(schedule.getTeam2RecordWins(), schedule.getTeam2RecordLosses()));

        Match match = new Match(schedule.getMatchId(), null, List.of(team1, team2),
                new Strategy(schedule.getMatchStrategyType(), schedule.getMatchStrategyCount()));

        return new LeagueScheduleResponseDto(schedule.getStartTime(), schedule.getState(), schedule.getType(),
                schedule.getBlockName(), league, match);
    }
}
