package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String startTime;
    private String state;
    private String type;
    private String blockName;
    private String leagueName;
    private String leagueSlug;
    @Column(nullable = false, unique = true)
    private String matchId;
//    private List<String> matchFlags; //영상유무
    private String team1Name;
    private String team1Code;
    private String team1Image;
    private String team1Outcome;
    private int team1GameWins;
    private int team1RecordWins;
    private int team1RecordLosses;
    private String team2Name;
    private String team2Code;
    private String team2Image;
    private String team2Outcome;
    private int team2GameWins;
    private int team2RecordWins;
    private int team2RecordLosses;
    private String matchStrategyType;
    private int matchStrategyCount;

    public Schedule(String startTime, String state, String type, String blockName, String leagueName, String leagueSlug, String matchId, String team1Name, String team1Code, String team1Image, String team1Outcome, int team1GameWins, int team1RecordWins, int team1RecordLosses, String team2Name, String team2Code, String team2Image, String team2Outcome, int team2GameWins, int team2RecordWins, int team2RecordLosses, String matchStrategyType, int matchStrategyCount) {
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
}
