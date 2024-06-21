package com.example.java21_test.service;

import com.example.java21_test.dto.requestDto.ProbabilityRequestDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "api 조회 및 승률예측값 저장")
@Service
@RequiredArgsConstructor
public class ProbabilityService {
    private final ApiService apiService;
    private final BetService betService;
    private final ScheduleRepository scheduleRepository;
    private final ProbabilityTransactionalService probabilityTransactionalService;

    @Value("${participant.roles}")
    String[] PARTICIPANT_ROLE;

    @Scheduled(cron = "0 0 0 * * ?")
    public void saveProbability() {
        List<Schedule> scheduleList = betService.getRecentTournamentSchedule();
        for (Schedule schedule : scheduleList) {
            String matchId = schedule.getMatchId();
            // eventDtail api 요청
            String jsonFromApi = apiService.getTeamDataJsonFromApi(matchId);
            // json 값을 통해 팀관련정보 준비(ds에 줄 정보)
            ProbabilityRequestDto probabilityRequestDto = createRequestDtoFromJson(jsonFromApi, matchId);
            // ds에 정보 전달 후 승률 받음
            String jsonFromDS = apiService.getProbabilityJsonFromDS(probabilityRequestDto);
            log.info(jsonFromDS);
            // 승률정보로 probability 저장
            probabilityTransactionalService.saveProbabilityFromJson(jsonFromDS, schedule);
        }
    }

    public ProbabilityRequestDto createRequestDtoFromJson(String json, String matchId) {
        log.info("json 문자열에서 Probability 값 가져오기");
        List<ProbabilityRequestDto.Participant> participantList = new ArrayList<>();
        List<ProbabilityRequestDto.TeamProbability> teamProbabilityList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode teamsNode = objectMapper.readTree(json).get("data").get("event").get("match").get("teams");
            for (JsonNode teamNode: teamsNode) {
                String teamId = teamNode.get("id").asText();
                String teamName = teamNode.get("name").asText();
                log.info(teamName);
                List<Schedule> scheduleList = scheduleRepository.findTop5ByStateAndTeamName("completed", teamName);
                List<JsonNode> participantsNodeList = new ArrayList<>();
                for (Schedule schedule : scheduleList) {
                    String matchIdBefore = schedule.getMatchId();
                    String jsonForGameId = apiService.getTeamDataJsonFromApi(matchIdBefore);
                    JsonNode participants = getParticipantDataFromJson(jsonForGameId, teamId);
                    participantsNodeList.add(participants);
                }
                Map<String, String> roleModeMap = findModeFromParticipantId(participantsNodeList);
                for (String role : PARTICIPANT_ROLE) {
                    ProbabilityRequestDto.Participant participant = new ProbabilityRequestDto.Participant(roleModeMap.get(role), role);
                    participantList.add(participant);
                }
                ProbabilityRequestDto.TeamProbability teamProbability = new ProbabilityRequestDto.TeamProbability(teamId, participantList);
                teamProbabilityList.add(teamProbability);
            }
            return new ProbabilityRequestDto(matchId, teamProbabilityList);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
        return null;
    }

    public JsonNode getParticipantDataFromJson(String json, String teamId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode gamesNode = objectMapper.readTree(json).get("data").get("event").get("match").get("games");
            // 첫 경기의 픽만 확인
            String gameId = gamesNode.get(0).get("id").asText();
            String jsonFromGame = apiService.getGameDataJsonFromApi(gameId);
            JsonNode blueTeamMetadata = objectMapper.readTree(jsonFromGame).get("gameMetadata").get("blueTeamMetadata");
            JsonNode redTeamMetadata = objectMapper.readTree(jsonFromGame).get("gameMetadata").get("redTeamMetadata");
            JsonNode participants;
            if (blueTeamMetadata.get("esportsTeamId").asText().equals(teamId)) {
                participants = blueTeamMetadata.get("participantMetadata");
            } else {
                participants = redTeamMetadata.get("participantMetadata");
            }
            return participants;
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
        return null;
    }

    public Map<String, String> findModeFromParticipantId(List<JsonNode> participantsList) {
        try{
            // 역할별 빈도수 맵
            Map<String, Map<String, Integer>> roleCountMap = new HashMap<>();
            for (JsonNode participants : participantsList) {
                for (JsonNode participant : participants) {
                    String role = participant.get("role").asText();
                    String participantId = participant.get("esportsPlayerId").asText();
                    roleCountMap.putIfAbsent(role, new HashMap<>());
                    Map<String, Integer> countMap = roleCountMap.get(role);
                    countMap.put(participantId, countMap.getOrDefault(participantId, 0) + 1);
                }
            }
            // 역할별 최빈값
            Map<String, String> roleModeMap = new HashMap<>();
            // 역할별
            for (Map.Entry<String, Map<String, Integer>> entry : roleCountMap.entrySet()) {
                String role = entry.getKey();
                Map<String, Integer> countMap = entry.getValue();
                int maxCount = 0;
                String mode = "";
                // 각 Id별로 count 확인
                for (Map.Entry<String, Integer> countEntry : countMap.entrySet()) {
                    if (countEntry.getValue() > maxCount) {
                        maxCount = countEntry.getValue();
                        mode = countEntry.getKey();
                    }
                }
                roleModeMap.put(role, mode);
            }
            return roleModeMap;

        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
        return null;
    }
}
