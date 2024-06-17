package com.example.java21_test.dto.mapper;

import com.example.java21_test.dto.responseDto.LeagueScheduleResponseDto;
import com.example.java21_test.entity.Schedule;

import java.util.List;

public class LeagueScheduleMapper {

    public static LeagueScheduleResponseDto toDto(Schedule schedule) {
        LeagueScheduleResponseDto.League league = new LeagueScheduleResponseDto.League(schedule.getLeagueName(), schedule.getLeagueSlug());

        LeagueScheduleResponseDto.Team team1 = new LeagueScheduleResponseDto.Team(schedule.getTeam1Name(), schedule.getTeam1Code(), schedule.getTeam1Image(),
                new LeagueScheduleResponseDto.Result(schedule.getTeam1Outcome(), schedule.getTeam1GameWins()),
                new LeagueScheduleResponseDto.Record(schedule.getTeam1RecordWins(), schedule.getTeam1RecordLosses()));

        LeagueScheduleResponseDto.Team team2 = new LeagueScheduleResponseDto.Team(schedule.getTeam2Name(), schedule.getTeam2Code(), schedule.getTeam2Image(),
                new LeagueScheduleResponseDto.Result(schedule.getTeam2Outcome(), schedule.getTeam2GameWins()),
                new LeagueScheduleResponseDto.Record(schedule.getTeam2RecordWins(), schedule.getTeam2RecordLosses()));

        LeagueScheduleResponseDto.Match match = new LeagueScheduleResponseDto.Match(schedule.getMatchId(), null, List.of(team1, team2),
                new LeagueScheduleResponseDto.Strategy(schedule.getMatchStrategyType(), schedule.getMatchStrategyCount()));

        return new LeagueScheduleResponseDto(schedule.getStartTime(), schedule.getState(), schedule.getType(),
                schedule.getBlockName(), league, match);
    }
}
