package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.ScheduleMapper;
import com.example.java21_test.dto.requestDto.ProbabilityRequestDto;
import com.example.java21_test.entity.Schedule;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "api 조회 및 승률예측값 저장")
@Service
@RequiredArgsConstructor
public class ProbabilityService {
    private ApiService apiService;

    public void saveProbability(Schedule schedule) {
        // requestDto 만들기
        ProbabilityRequestDto.Participant participant = new ProbabilityRequestDto.Participant(5, "fsdf", "sdfs", "sdfsd", "sfs");
        List<ProbabilityRequestDto.Participant> participantList = new ArrayList<>();
        ProbabilityRequestDto.TeamProbability teamProbability = new ProbabilityRequestDto.TeamProbability("fds", participantList);

        List<ProbabilityRequestDto.TeamProbability> teamProbabilityList = new ArrayList<>();
        ProbabilityRequestDto probabilityRequestDto = new ProbabilityRequestDto("", teamProbabilityList);

        String json = apiService.getProbabilityJsonFromDS(probabilityRequestDto);

    }

//    @Transactional
//    public void saveProbabilityFromJson(String json) {
//        log.info("json 문자열에서 Probability 값 가져오기");
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode events = objectMapper.readTree(json).get("data").get("schedule").get("events");
//            // newer 확인
//            String newer = objectMapper.readTree(json).get("data").get("schedule").get("pages").get("newer").asText();
//            if (!newer.equals("null")) {
//                log.info("추가 페이지 확인!");
//                // 재귀로 추가 페이지가 없을 때 까지 불러온다.
//                saveScheduleFromJson(apiService.getScheduleJsonFromApi(leagueId, newer), leagueId);
//            }
//            for (JsonNode rootNode : events) {
//                log.info(rootNode.asText());
//                // JSON 데이터에서 필요한 정보 추출, Schedule 객체 생성 저장
//                Schedule schedule = ScheduleMapper.toDto(rootNode);
//                String matchId = schedule.getMatchId();
//                Schedule checkSchedule = scheduleRepository.findByMatchId(matchId).orElse(null);
//                if (checkSchedule == null) { // 중복 값이 없는 경우 저장
//                    scheduleRepository.save(schedule);
//                } else { // 중복인 경우 업데이트
//                    checkSchedule.update(schedule);
//                }
//                // betting page에 들어가는 리그인지 확인
//                if (majorLeagueList.contains(leagueId)) {
//                    // 승률예측 값 받아오기
//                    // 값 있는지, tbd는 아닌지 확인
//                    //
//
//                    // 날짜가 오늘인 경우 스케줄 일정 등록...
//                    apiUpdateService.checkTodaySchedule(schedule, leagueId);
//                }
//
//                // 날짜가 오늘인 경우 스케줄 일정 등록...
//                apiUpdateService.checkTodaySchedule(schedule, leagueId);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 처리
//        }
//    }
}
