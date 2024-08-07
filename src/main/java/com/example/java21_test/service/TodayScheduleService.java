package com.example.java21_test.service;

import com.example.java21_test.entity.Schedule;
import com.example.java21_test.entity.Tournament;
import com.example.java21_test.respository.ScheduleRepository;
import com.example.java21_test.respository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j(topic = "todaySchedule service")
@Service
@RequiredArgsConstructor
public class TodayScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TournamentRepository tournamentRepository;
    private final TaskScheduler taskScheduler;
    private final PointService pointService;
    private final ApiService apiService;
    private final ScheduleTransactionalService scheduleTransactionalService;
    private final SseTransactionalService sseTransactionalService;
    private final SseService sseService;

    // 날짜가 오늘인 경우 스케줄 일정 등록...
    @EventListener(ApplicationReadyEvent.class)
    public void checkTodaySchedule() {
        log.info("현재시각 : " + LocalDateTime.now());
        // 현재 날짜 가져오기
        String localDateNow = LocalDate.now().toString();
        // 최근 토너먼트 가져오기
        Tournament tournament = getRecentTournament(localDateNow);
        log.info(tournament.getSlug());
        // 오늘의 경기일정
        List<Schedule> todayScheduleList = getTodaySchedule(tournament, localDateNow);
        log.info("오늘의 경기수 : " + todayScheduleList.size());
        if (!todayScheduleList.isEmpty()) {
            for (Schedule schedule : todayScheduleList) {
                createScheduleTaskAt(schedule);
            }
        }
    }

    // 1차 스케줄(경기시작시 루프를 생성할 스케줄러) 생성
    public void createScheduleTaskAt(Schedule schedule) {
        Instant startTime = Instant.parse(schedule.getStartTime());
        log.info(startTime.toString() + "에 시작하는 일정 등록");
        if (taskScheduler != null) {
            taskScheduler.schedule(() -> roofScheduleTaskAt(schedule), startTime);
        } else {
            log.error("TaskScheduler is null. Cannot schedule task.");
        }
    }
    // 2차 스케줄(루프를 돌면서 경기기록을 지속적으로 확인하는 자기자신을 호출)
    @Async
    public void roofScheduleTaskAt(Schedule schedule) {
        log.info("updating schedule");
        // 처음 경기 시작시에만 보내는 알림
        if (schedule.getState().equals("unstarted")) {
            sseTransactionalService.saveGameStartEvent(schedule);
            sseService.sendNoticeAll();
        }
        //이 사이에 조회 업데이트로직이 들어감
        String json = apiService.getEventDetailJsonFromApi(schedule.getMatchId());
        scheduleTransactionalService.updateScheduleFromJson(json, schedule);

        boolean isCompleted = schedule.getState().equals("completed");

        if (!isCompleted) {
            // 아직 경기가 끝나지 않아서 10분뒤 다시 자신이 작동하도록 호출함
            Instant checkTime = Instant.now().plusSeconds(300); // 5분 뒤에 다시 실행
            taskScheduler.schedule(() -> roofScheduleTaskAt(schedule), checkTime);
        } else {
            // 배당금 분배
            pointService.checkOdds(schedule.getMatchId());
        }
    }

    public List<Schedule> getTodaySchedule(Tournament tournament, String localDateNow) {
        String slug = tournament.getSlug().split("_")[0];
        return scheduleRepository.findAllByLeagueSlugAndStartDate(slug, localDateNow);
    }

    public Tournament getRecentTournament(String localDateNow) {

        // tournament에서 가장 최근 일정을 가져온다
        Tournament tournament = tournamentRepository.findTop1ByEndDateAfterOrderByStartDateAsc(localDateNow).orElse(null);
        if (tournament == null) { //오늘기준 이전 토너먼트는 끝났지만 아직 새로운 토너먼트 일정이 없는 경우
            // 여기서도 없으면 이건 500 서버에러로 처리 가장 날짜가 늦은(최근에 진행된) 토너먼트
            tournament = tournamentRepository.findTopByOrderByStartDateDesc().orElseThrow(() ->
                    new NullPointerException("토너먼트 일정이 없음"));
        }
        return tournament;
    }
}
