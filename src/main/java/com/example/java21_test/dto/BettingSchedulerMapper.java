package com.example.java21_test.dto;

import com.example.java21_test.entity.Schedule;

import java.util.List;

public class BettingSchedulerMapper{
    public static BettingScheduleResponseDto toDto(Schedule schedule, boolean betting, int amount, String teamCode, String status, float ratio1, float ratio2)  {
        League league = new League(schedule.getLeagueName(), schedule.getLeagueSlug());

        TeamRatio team1 = new TeamRatio(schedule.getTeam1Name(), schedule.getTeam1Code(), schedule.getTeam1Image(),
                new Result(schedule.getTeam1Outcome(), schedule.getTeam1GameWins()),
                new Record(schedule.getTeam1RecordWins(), schedule.getTeam1RecordLosses()), ratio1);


        TeamRatio team2 = new TeamRatio(schedule.getTeam2Name(), schedule.getTeam2Code(), schedule.getTeam2Image(),
                new Result(schedule.getTeam2Outcome(), schedule.getTeam2GameWins()),
                new Record(schedule.getTeam2RecordWins(), schedule.getTeam2RecordLosses()), ratio2);

        MatchRatio matchRatio = new MatchRatio(schedule.getMatchId(), betting, amount, teamCode, status, List.of(team1, team2),
                new Strategy(schedule.getMatchStrategyType(), schedule.getMatchStrategyCount()));

        return new BettingScheduleResponseDto(schedule.getStartTime(), schedule.getState(), schedule.getType(),
                schedule.getBlockName(), league, matchRatio);
    }
}
