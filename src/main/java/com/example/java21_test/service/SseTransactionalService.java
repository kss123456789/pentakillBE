package com.example.java21_test.service;

import com.example.java21_test.entity.PointLog;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.entity.SseNotice;
import com.example.java21_test.respository.SseNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "sse transactional")
@Service
@RequiredArgsConstructor
public class SseTransactionalService {
    private final SseNoticeRepository sseNoticeRepository;

    @Transactional
    public void saveGameStartEvent(Schedule schedule) {
        String eventName = "matchStatusNotice";
        String team1Name = schedule.getTeam1Name();
        String team2Name = schedule.getTeam2Name();

        SseNotice sseNotice = new SseNotice(eventName, team1Name, team2Name);

        sseNoticeRepository.save(sseNotice);
    }

    @Transactional
    public void saveGameEndEvent(Schedule schedule, PointLog pointLog) {
        String eventName = "matchResultNotice";
        String team1Name = schedule.getTeam1Name();
        String team2Name = schedule.getTeam2Name();
        int point = pointLog.getAmount();
        String outcome = pointLog.getStatus();

        SseNotice sseNotice = new SseNotice(eventName, team1Name, team2Name, point, outcome);

        sseNoticeRepository.save(sseNotice);
    }

    @Transactional
    public void deleteNotice(List<SseNotice> sseNoticeList) {
        sseNoticeRepository.deleteAll(sseNoticeList);
    }
}
