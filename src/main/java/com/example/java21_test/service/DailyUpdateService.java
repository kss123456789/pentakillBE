package com.example.java21_test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j(topic = "daily update service")
@Service
@RequiredArgsConstructor
public class DailyUpdateService {

    private final ScheduleService scheduleService;
    private final BetService betService;
    private final TodayScheduleService todayScheduleService;
    private final ProbabilityService probabilityService;
    private final S3Service s3Service;

    // 매일 자정에 자동 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyUpdate() {
        //경기일정 업데이트
        scheduleService.saveLeagueSchedules();
        //토너먼트 업데이트
        betService.saveTournaments();
        //오늘의 경기 등록
        todayScheduleService.checkTodaySchedule();
        //승률 업데이트
        probabilityService.saveProbability();
        //s3 임시파일 삭제
        s3Service.deleteOldTempFiles();
    }

}
