package com.example.java21_test.dto.mapper;

import com.example.java21_test.dto.responseDto.BettingScheduleResponseDto;
import com.example.java21_test.dto.responseDto.LeagueScheduleResponseDto;
import com.example.java21_test.entity.Schedule;

import java.util.List;

public class BettingSchedulerMapper{

    public static BettingScheduleResponseDto toDto(Schedule schedule, boolean betting, int amount, String teamCode, String status,
                                                   float ratio1, float ratio2, float probability1, float probability2)  {
        LeagueScheduleResponseDto.League league = new LeagueScheduleResponseDto.League(schedule.getLeagueName(), schedule.getLeagueSlug());

        BettingScheduleResponseDto.TeamRatio team1 = new BettingScheduleResponseDto.TeamRatio(schedule.getTeam1Name(), schedule.getTeam1Code(), schedule.getTeam1Image(),
                new LeagueScheduleResponseDto.Result(schedule.getTeam1Outcome(), schedule.getTeam1GameWins()),
                new LeagueScheduleResponseDto.Record(schedule.getTeam1RecordWins(), schedule.getTeam1RecordLosses()), ratio1, probability1);


        BettingScheduleResponseDto.TeamRatio team2 = new BettingScheduleResponseDto.TeamRatio(schedule.getTeam2Name(), schedule.getTeam2Code(), schedule.getTeam2Image(),
                new LeagueScheduleResponseDto.Result(schedule.getTeam2Outcome(), schedule.getTeam2GameWins()),
                new LeagueScheduleResponseDto.Record(schedule.getTeam2RecordWins(), schedule.getTeam2RecordLosses()), ratio2, probability2);

        BettingScheduleResponseDto.MatchRatio matchRatio = new BettingScheduleResponseDto.MatchRatio(schedule.getMatchId(), betting, amount, teamCode, status, List.of(team1, team2),
                new LeagueScheduleResponseDto.Strategy(schedule.getMatchStrategyType(), schedule.getMatchStrategyCount()));

        return new BettingScheduleResponseDto(schedule.getStartTime(), schedule.getState(), schedule.getType(),
                schedule.getBlockName(), league, matchRatio);
    }
}
