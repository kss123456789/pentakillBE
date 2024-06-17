package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.LeagueScheduleMapper;
import com.example.java21_test.dto.mapper.ScheduleMapper;
import com.example.java21_test.dto.responseDto.LeagueScheduleResponseDto;
import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "openApi schedule 저장, db조회")
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ApiService apiService;
    private final ApiUpdateService apiUpdateService;

//    api 읽기, 값 저장, 값 가져오기, leagueId 확인
    @Value("${league.ids}")
    private List<String> leagueIdList;

    @Value("${majorLeague.ids}")
    private List<String> majorLeagueList;

    // 하루의 시작... 12시에 업데이트, 그날의 경기가 있다면 미리 스케줄 등록
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void saveLeagueSchedules() {
        log.info("리그스케쥴 업데이트");
        for (String leagueId : leagueIdList) {
            String json = apiService.getScheduleJsonFromApi(leagueId, null);
            saveScheduleFromJson(json, leagueId);
        }
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
                // 재귀로 추가 페이지가 없을 때 까지 불러온다.
                saveScheduleFromJson(apiService.getScheduleJsonFromApi(leagueId, newer), leagueId);
            }
            for (JsonNode rootNode : events) {
                log.info(rootNode.asText());
                // JSON 데이터에서 필요한 정보 추출, Schedule 객체 생성 저장
                Schedule schedule = ScheduleMapper.toDto(rootNode);
                String matchId = schedule.getMatchId();
                Schedule checkSchedule = scheduleRepository.findByMatchId(matchId).orElse(null);
                if (checkSchedule == null) { // 중복 값이 없는 경우 저장
                    scheduleRepository.save(schedule);
                } else { // 중복인 경우 업데이트
                    checkSchedule.update(schedule);
                }
                // betting page에 들어가는 리그인지 확인
                if (majorLeagueList.contains(leagueId)) {
                    // 승률예측 값 받아오기
                    // 값 있는지, tbd는 아닌지 확인
                    //

                    // 날짜가 오늘인 경우 스케줄 일정 등록...
                    apiUpdateService.checkTodaySchedule(schedule, leagueId);
                }

                // 날짜가 오늘인 경우 스케줄 일정 등록...
                apiUpdateService.checkTodaySchedule(schedule, leagueId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
    }
}