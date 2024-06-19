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
        private String esportsPlayerId;
        private String role;

        public Participant(String esportsPlayerId, String role) {
            this.esportsPlayerId = esportsPlayerId;
            this.role = role;
        }
    }

    public ProbabilityRequestDto(String matchId, List<TeamProbability> teams) {
        this.matchId = matchId;
        this.teams = teams;
    }

}
