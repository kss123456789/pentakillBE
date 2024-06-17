package com.example.java21_test.dto.requestDto;

import com.example.java21_test.dto.responseDto.LeagueScheduleResponseDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ProbabilityRequestDto {
    private String matchId;
    private List<TeamProbability> teams;

    @Getter
    public static class TeamProbability {
        private String esportsTeamId;
        private List<Participant> participantMetadata;

        public TeamProbability(String esportsTeamId, List<Participant> participantMetadata) {
            this.esportsTeamId = esportsTeamId;
            this.participantMetadata = participantMetadata;
        }
    }

    @Getter
    public static class Participant {
        private int participantId;
        private String esportsPlayerId;
        private String summonerName;
        private String championId;
        private String role;

        public Participant(int participantId, String esportsPlayerId, String summonerName, String championId, String role) {
            this.participantId = participantId;
            this.esportsPlayerId = esportsPlayerId;
            this.summonerName = summonerName;
            this.championId = championId;
            this.role = role;
        }
    }

    public ProbabilityRequestDto(String matchId, List<TeamProbability> teams) {
        this.matchId = matchId;
        this.teams = teams;
    }

}
