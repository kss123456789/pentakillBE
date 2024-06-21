package com.example.java21_test.service;

import com.example.java21_test.entity.Tournament;
import com.example.java21_test.respository.TournamentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "openApi tournaments 저장")
@Service
@RequiredArgsConstructor
public class TournamentTransactionalService {
    private final TournamentRepository tournamentRepository;
    @Transactional
    public void saveTournamentsFromJson(String json) {
        log.info("json 문자열을 Tournament로 변환");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 가장 가까운 날짜의 토너먼트를 가져옴
            JsonNode latestTournaments = objectMapper.readTree(json).get("data").get("leagues").get(0).get("tournaments").get(0);
            String id = latestTournaments.get("id").asText();
            // 중복확인
            Tournament checkTournament = tournamentRepository.findById(id).orElse(null);

            if (checkTournament == null) { // 중복 값이 없는 경우 저장
                String slug = latestTournaments.get("slug").asText();
                String startDate = latestTournaments.get("startDate").asText();
                String endDate = latestTournaments.get("endDate").asText();

                Tournament tournament = new Tournament(id, slug, startDate, endDate);
                tournamentRepository.save(tournament);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
    }
}
