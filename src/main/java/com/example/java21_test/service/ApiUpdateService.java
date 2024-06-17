package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.ScheduleMapper;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j(topic = "api update service")
@Service
@RequiredArgsConstructor
public class ApiUpdateService {
    private final ScheduleRepository scheduleRepository;
    private final TaskScheduler taskScheduler;
    private final PointService pointService;
    private final ApiService apiService;

    // 날짜가 오늘인 경우 스케줄 일정 등록...
    @Transactional
    public void checkTodaySchedule(Schedule schedule, String leagueId) {
        Instant instantStart = Instant.parse(schedule.getStartTime());
        LocalDate localStartDate = instantStart.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
        if (LocalDate.now().isEqual(localStartDate)) {
            createScheduleTaskAt(instantStart, schedule, leagueId);
        }
    }

    // 1차 스케줄(경기시작시 루프를 생성할 스케줄러) 생성
    @Transactional
    public void createScheduleTaskAt(Instant startTime, Schedule schedule, String leagueId) {
        taskScheduler.schedule(() -> roofScheduleTaskAt(schedule, leagueId), startTime);
    }
    // 2차 스케줄(루프를 돌면서 경기기록을 지속적으로 확인하는 자기자신을 호출)
    @Transactional
    public void roofScheduleTaskAt(Schedule schedule, String leagueId) {

        //이 사이에 조회 업데이트로직이 들어감 //단순하게 그냥 그 토너먼트를 전체 업데이트 시도하도록 수정...
        String json = apiService.getScheduleJsonFromApi(leagueId, null);
        updateScheduleFromJson(json, leagueId);
        scheduleRepository.flush();

        boolean isCompleted = schedule.getState().equals("completed");

        if (!isCompleted) {
            // 아직 경기가 끝나지 않아서 10분뒤 다시 자신이 작동하도록 호출함
            Instant checkTime = Instant.now().plusSeconds(600); // 10분 뒤에 다시 실행
            taskScheduler.schedule(() -> roofScheduleTaskAt(schedule, leagueId), checkTime);
        } else {
            // 배당금 분배후
            pointService.checkOdds(schedule.getMatchId());
        }
    }

    @Transactional
    public void updateScheduleFromJson(String json, String leagueId) {
        log.info("json 문자열을 schedule객체로 변환");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode events = objectMapper.readTree(json).get("data").get("schedule").get("events");
            // newer 확인
            String newer = objectMapper.readTree(json).get("data").get("schedule").get("pages").get("newer").asText();
            if (!newer.equals("null")) {
                log.info("추가 페이지 확인!");
                // 재귀로 추가 페이지가 없을 때 까지 불러온다.
                updateScheduleFromJson(apiService.getScheduleJsonFromApi(leagueId, newer), leagueId);
            }
            for (JsonNode rootNode : events) {
                // JSON 데이터에서 필요한 정보() 추출
                Schedule schedule = ScheduleMapper.toDto(rootNode);
                String matchId = schedule.getMatchId();
                Schedule checkSchedule = scheduleRepository.findByMatchId(matchId).orElseThrow(NullPointerException::new);

                checkSchedule.update(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
    }


}
