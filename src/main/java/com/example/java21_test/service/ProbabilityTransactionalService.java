package com.example.java21_test.service;

import com.example.java21_test.entity.Probability;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ProbabilityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "openApi schedule 저장, 업데이트")
@Service
@RequiredArgsConstructor
public class ProbabilityTransactionalService {
    private final ProbabilityRepository probabilityRepository;
    @Transactional
    public void saveProbabilityFromJson(String json, Schedule schedule) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            double team1winrate = objectMapper.readTree(json).get("team1").get("win_rate").asDouble();
            double team2winrate = objectMapper.readTree(json).get("team2").get("win_rate").asDouble();

            Probability probability = new Probability(team1winrate, team2winrate, schedule);
            probabilityRepository.save(probability);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DS로 부터 받은 json에 문제가 있습니다.");
            // 예외 처리
        }
    }
}
