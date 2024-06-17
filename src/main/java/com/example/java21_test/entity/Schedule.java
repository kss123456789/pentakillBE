package com.example.java21_test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Schedule {
    private String startTime;
    private String state;
    private String type;
    private String blockName;
    private String leagueName;
    private String leagueSlug;

    @Id
    private String matchId;
//    private List<String> matchFlags; //영상유무
    private String team1Name;
    private String team1Code;
    private String team1Image;
    private String team1Outcome;
    private Integer team1GameWins;
    private Integer team1RecordWins;
    private Integer team1RecordLosses;
    private String team2Name;
    private String team2Code;
    private String team2Image;
    private String team2Outcome;
    private Integer team2GameWins;
    private Integer team2RecordWins;
    private Integer team2RecordLosses;
    private String matchStrategyType;
    private Integer matchStrategyCount;

    @OneToMany(mappedBy = "schedule")
    private List<PointLog> pointLogList = new ArrayList<>();

    @OneToOne(mappedBy = "schedule")
    private Probability probability;

    public Schedule(String startTime, String state, String type, String blockName, String leagueName, String leagueSlug, String matchId,
                    String team1Name, String team1Code, String team1Image, String team1Outcome,
                    int team1GameWins, int team1RecordWins, int team1RecordLosses,
                    String team2Name, String team2Code, String team2Image, String team2Outcome,
                    int team2GameWins, int team2RecordWins, int team2RecordLosses,
                    String matchStrategyType, int matchStrategyCount) {
        this.startTime = startTime;
        this.state = state;
        this.type = type;
        this.blockName = blockName;
        this.leagueName = leagueName;
        this.leagueSlug = leagueSlug;
        this.matchId = matchId;
        this.team1Name = team1Name;
        this.team1Code = team1Code;
        this.team1Image = team1Image;
        this.team1Outcome = team1Outcome;
        this.team1GameWins = team1GameWins;
        this.team1RecordWins = team1RecordWins;
        this.team1RecordLosses = team1RecordLosses;
        this.team2Name = team2Name;
        this.team2Code = team2Code;
        this.team2Image = team2Image;
        this.team2Outcome = team2Outcome;
        this.team2GameWins = team2GameWins;
        this.team2RecordWins = team2RecordWins;
        this.team2RecordLosses = team2RecordLosses;
        this.matchStrategyType = matchStrategyType;
        this.matchStrategyCount = matchStrategyCount;
    }

    public void update(Schedule schedule) {
        this.startTime = schedule.getStartTime();
        this.state = schedule.getState();
        this.type = schedule.getType();
        this.blockName = schedule.getBlockName();
        this.leagueName = schedule.getLeagueName();
        this.leagueSlug = schedule.getLeagueSlug();
        this.team1Name = schedule.getTeam1Name();
        this.team1Code = schedule.getTeam1Code();
        this.team1Image = schedule.getTeam1Image();
        this.team1Outcome = schedule.getTeam1Outcome();
        this.team1GameWins = schedule.getTeam1GameWins();
        this.team1RecordWins = schedule.getTeam1RecordWins();
        this.team1RecordLosses = schedule.getTeam1RecordLosses();
        this.team2Name = schedule.getTeam2Name();
        this.team2Code = schedule.getTeam2Code();
        this.team2Image = schedule.getTeam2Image();
        this.team2Outcome = schedule.getTeam2Outcome();
        this.team2GameWins = schedule.getTeam2GameWins();
        this.team2RecordWins = schedule.getTeam2RecordWins();
        this.team2RecordLosses = schedule.getTeam2RecordLosses();
        this.matchStrategyType = schedule.getMatchStrategyType();
        this.matchStrategyCount = schedule.getMatchStrategyCount();
    }
}
