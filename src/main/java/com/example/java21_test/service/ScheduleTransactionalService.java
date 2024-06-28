package com.example.java21_test.service;

import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "openApi schedule 저장, 업데이트")
@Service
@RequiredArgsConstructor
public class ScheduleTransactionalService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void saveSchedule(Schedule schedule) {
        String matchId = schedule.getMatchId();
        Schedule checkSchedule = scheduleRepository.findByMatchId(matchId).orElse(null);
        if (checkSchedule == null) { // 중복 값이 없는 경우 저장
            scheduleRepository.save(schedule);
        } else { // 중복인 경우 업데이트
            checkSchedule.update(schedule);
        }
    }

    @Transactional
    public void updateScheduleFromJson(String json, Schedule schedule) {
        log.info("json 문자열을 schedule객체로 변환");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode matchNode = objectMapper.readTree(json).get("data").get("event").get("match");
            // completed 조건
            float matchEndWin = matchNode.get("strategy").get("count").asInt()/2f;
            String team1Outcome = null;
            String team2Outcome = null;
            // 각 팀 매치 승리수
            JsonNode team1Node = matchNode.get("teams").get(0);
            String team1Name = team1Node.get("name").asText();
            int team1wins = team1Node.get("result").get("gameWins").asInt();
            JsonNode team2Node = matchNode.get("teams").get(1);
            String team2Name = team2Node.get("name").asText();
            int team2wins = team2Node.get("result").get("gameWins").asInt();
            log.info(team1wins + " : " + team2wins);
            String state = "inProgress";
            if (team1wins > matchEndWin) {
                state = "completed";
                team1Outcome = "win";
                team2Outcome = "loss";

            } else if (team2wins > matchEndWin) {
                state = "completed";
                team1Outcome = "loss";
                team2Outcome = "win";
            }
            if (schedule.getTeam1Name().equals(team1Name) && schedule.getTeam2Name().equals(team2Name)) {
                schedule.updateLive(state, team1wins, team2wins, team1Outcome, team2Outcome);
            } else if (schedule.getTeam1Name().equals(team2Name) && schedule.getTeam2Name().equals(team1Name)){
                schedule.updateLive(state, team2wins, team1wins, team2Outcome, team1Outcome);
            } else {
                // 잘못된 schedule을 가져온 사례
                throw new RuntimeException("업데이트에 문제 발생 로그를 확인하세요");
            }

            // 변경된 스케줄을 저장
            scheduleRepository.save(schedule);
            log.info("Schedule updated successfully");

        } catch (Exception e) {
            log.error("Error updating schedule from JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Schedule update failed", e);
        }
    }
}
