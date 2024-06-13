package com.example.java21_test.service;

import com.example.java21_test.dto.requestDto.PointBettngRequestDto;
import com.example.java21_test.dto.responseDto.PointLogResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import com.example.java21_test.respository.ScheduleRepository;
import com.example.java21_test.util.PointUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j(topic = "point 배팅, 포인트분배 등 포인트 관련")
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointUtil pointUtil;

    // point betting 경기가 시작하지 않은걸 확인하고 betting시작
    // 현재 테스트 용도로 쓰기위해 남겨두고 주석처리 해둘예정임
    @Transactional
    public StatusCodeResponseDto<PointLogResponseDto> pointBetting(PointBettngRequestDto pointBettngRequestDto, User user) {
        int amount = pointBettngRequestDto.getPoint();
        String matchId = pointBettngRequestDto.getMatchId();
        String teamCode = pointBettngRequestDto.getTeamCode();
        log.info("checkloog2");
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
        );
        // schedule을 다른 table들과 연관관계해서 하려한다... 그리고 schedule에서 경기가 아직 시작하지 않았는지 확인후 배팅을 수행하도록 수정
        // foregin key의 경우 null이 허용되므로 null인경우도 쓸 수 있다.
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
        if (!schedule.getState().equals("unstarted")) {
            return new StatusCodeResponseDto<>(HttpStatus.BAD_REQUEST.value(), "schedule is started");
        }
        PointLog pointLog = new PointLog(amount, teamCode, schedule.getState(), schedule, point);
        point.update(-amount);
        pointLogRepository.save(pointLog);
        pointLog = pointLogRepository.findByScheduleAndPoint(schedule, point).orElseThrow(() ->
                new IllegalArgumentException("기록을 찾을 수 없습니다.")
        ); //Query did not return a unique result: 2 results were returned 이미 배팅한 경기에 또다시 배팅하려고 할때

        PointLogResponseDto pointLogResponseDto = new PointLogResponseDto(pointLog);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "point log saved", pointLogResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<Void> checkOdds(String matchId) {
        // 한번에 모든 사람들에게 배당금지급하도록 수정
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
        String team1Outcome = schedule.getTeam1Outcome();
        String team2Outcome = schedule.getTeam2Outcome();
        String team1Code = schedule.getTeam1Code();
        String team2Code = schedule.getTeam2Code();
        log.info(schedule.getState());
        int count = 0;
        if (schedule.getState().equals("completed")) {
            List<PointLog> pointLogList = pointLogRepository.findAllBySchedule(schedule);
            Map<String, Float> oddsMap = getOdds(schedule, pointLogList);
            for (PointLog pointLog : pointLogList) {
                String status = pointLog.getStatus();
                if (!status.equals("unstarted")) {
                    continue;
                }
                String bettingTeam = pointLog.getTeamCode();
                if ((team1Outcome.equals("win") && bettingTeam.equals(team1Code))
                        || (team2Outcome.equals("win") && bettingTeam.equals(team2Code))) {
                    float odds = oddsMap.get(bettingTeam);
                    pointUtil.winPoint(pointLog, odds);
                    count++;
                }
                else {
                    pointUtil.lossPoint(pointLog);
                }
            }
            // 0, 1 대신 1, 2로 바뀔가능성 있음

        }
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), String.format("%d개의 배당금이 분배되었습니다.", count));
    }

    private Map<String, Float> getOdds(Schedule schedule, List<PointLog> pointLogList) {
        long betting1 = 0L;
        long betting2 = 0L;
        String team1Code = schedule.getTeam1Code();
        String team2Code = schedule.getTeam2Code();
        for (PointLog pointLog : pointLogList) {
            int amount = pointLog.getAmount();
            String bettingTeam = pointLog.getTeamCode();
            if (bettingTeam.equals(team1Code)) {
                betting1 += amount;
            }
            else {
                betting2 += amount;
            }
        }
        float odd1 = (betting1 + betting2) / (float) betting1;
        float odd2 = (betting1 + betting2) / (float) betting2;
        Map<String, Float> oddsMap = new HashMap<>();
        oddsMap.put(team1Code, odd1);
        oddsMap.put(team2Code, odd2);
        return oddsMap;
    }
}
