package com.example.java21_test.service;

import com.example.java21_test.dto.LeagueScheduleMapper;
import com.example.java21_test.dto.LeagueScheduleResponseDto;
import com.example.java21_test.dto.PageResponseDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.example.java21_test.util.RestTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "openApi schedule 저장, db조회")
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final RestTemplateUtil restTemplateUtil;
//    api 읽기, 값 저장, 값 가져오기, leagueId 확인

    @Transactional
    public PageResponseDto<LeagueScheduleResponseDto> saveLeagueSchedules() {
        log.info("리그스케쥴 업데이트");
        List<String> leagueIdList = new ArrayList<>();
        leagueIdList.add("98767975604431411"); //world
        leagueIdList.add("98767991325878492"); //msi
        leagueIdList.add("98767991310872058"); //lck
        leagueIdList.add("98767991314006698"); //lpl
        leagueIdList.add("98767991302996019"); //lec
        leagueIdList.add("98767991299243165"); //lcs

        for (String leagueId : leagueIdList) {
            saveLeagueSchedulesFromApi(leagueId, null);
        }

        return getLeagueSchedules(10, 0);
    }

    public PageResponseDto<LeagueScheduleResponseDto> getLeagueSchedules(Integer size, Integer page) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Schedule> scheduleList = scheduleRepository.findAllByOrderByStartTimeDesc(pageable);
        if (scheduleList.isEmpty()) {
            // 비어 있다면 적절한 응답을 반환
            return new PageResponseDto<>(HttpStatus.NOT_FOUND.value(), "No schedules found for the league");
        }
        Page<LeagueScheduleResponseDto> leagueScheduleResponseDto = scheduleList.map(LeagueScheduleMapper::toDto);

        return new PageResponseDto<>(HttpStatus.OK.value(), "SUCCESS", leagueScheduleResponseDto);
    }

    @Transactional
    public void saveLeagueSchedulesFromApi(String leagueId, String newer) {
        log.info("특정리그 schedule 가져오기 from api");

        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://esports-api.lolesports.com")
                .path("/persisted/gw/getSchedule")
                .queryParam("hl", "ko-KR")
                .queryParam("leagueId", leagueId)
                .queryParam("pageToken", newer)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getDataFromAPI(targetUrl);

        saveScheduleFromJson(result.getBody(), leagueId);
    }

    // 문제... try catch로 중복값 에러처리를 하고있는데 이걸 확인하고 넣도록 해서 진행중인데.. 만일 json 값중 위에 있는 값이 중복값이 있으면 아래에 있는 중복 아닌값도 추가가 안되게 된다.
    // 해결책1 근본일지도 모르나 가장 성능은 구릴것 같은것 같은 방법 저장하기 전에 중복확인을 하고 
    @Transactional
    public void saveScheduleFromJson(String json, String leagueId) {
        log.info("json 문자열을 schedule객체로 변환");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode events = objectMapper.readTree(json).get("data").get("schedule").get("events");
            // newer 확인
            String newer = objectMapper.readTree(json).get("data").get("schedule").get("pages").get("newer").asText();
            if (!newer.equals("null")) {
                log.info("추가 페이지 확인!");
                saveLeagueSchedulesFromApi(leagueId, newer);
            }
            for (JsonNode rootNode : events) {
                // JSON 데이터에서 필요한 정보 추출
                String matchId = rootNode.get("match").get("id").asText();

                Schedule checkSchedule = scheduleRepository.findByMatchId(matchId).orElse(null);
                if (checkSchedule == null) { // 중복 값이 없는 경우 저장
                    String startTime = rootNode.get("startTime").asText();
                    String state = rootNode.get("state").asText();
                    String type = rootNode.get("type").asText();
                    String blockName = rootNode.get("blockName").asText();
                    String leagueName = rootNode.get("league").get("name").asText();
                    String leagueSlug = rootNode.get("league").get("slug").asText();

                    JsonNode teamsNode = rootNode.get("match").get("teams");

                    String team1Name = teamsNode.get(0).get("name").asText();
                    String team1Code = teamsNode.get(0).get("code").asText();
                    String team1Image = teamsNode.get(0).get("image").asText();
                    // result, record가 null인 경우 발견
                    String team1Outcome = null;
                    int team1GameWins = 0;
                    int team1RecordWins = 0;
                    int team1RecordLosses = 0;
                    if (!teamsNode.get(0).get("result").isNull()) {
                        team1Outcome = teamsNode.get(0).get("result").get("outcome").asText();
                        team1GameWins = teamsNode.get(0).get("result").get("gameWins").asInt();
                        team1RecordWins = teamsNode.get(0).get("record").get("wins").asInt();
                        team1RecordLosses = teamsNode.get(0).get("record").get("losses").asInt();
                    }

                    String team2Name = teamsNode.get(1).get("name").asText();
                    String team2Code = teamsNode.get(1).get("code").asText();
                    String team2Image = teamsNode.get(1).get("image").asText();
                    String team2Outcome = null;
                    int team2GameWins = 0;
                    int team2RecordWins = 0;
                    int team2RecordLosses = 0;
                    if (!teamsNode.get(1).get("result").isNull()) {
                        team2Outcome = teamsNode.get(1).get("result").get("outcome").asText();
                        team2GameWins = teamsNode.get(1).get("result").get("gameWins").asInt();
                        team2RecordWins = teamsNode.get(1).get("record").get("wins").asInt();
                        team2RecordLosses = teamsNode.get(1).get("record").get("losses").asInt();
                    }

                    String matchStrategyType = rootNode.get("match").get("strategy").get("type").asText();
                    int matchStrategyCount = rootNode.get("match").get("strategy").get("count").asInt();

                    // Schedule 객체 생성 및 저장
                    Schedule schedule = new Schedule(startTime, state, type, blockName, leagueName, leagueSlug, matchId,
                            team1Name, team1Code, team1Image, team1Outcome, team1GameWins, team1RecordWins, team1RecordLosses,
                            team2Name, team2Code, team2Image, team2Outcome, team2GameWins, team2RecordWins, team2RecordLosses,
                            matchStrategyType, matchStrategyCount);
                    scheduleRepository.save(schedule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
    }
}