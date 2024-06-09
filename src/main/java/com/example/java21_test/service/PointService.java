package com.example.java21_test.service;

import com.example.java21_test.dto.PointBettngRequestDto;
import com.example.java21_test.dto.PointLogResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
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
    public StatusCodeResponseDto pointBetting(PointBettngRequestDto pointBettngRequestDto, User user) {
        int amount = pointBettngRequestDto.getPoint();
        String matchId = pointBettngRequestDto.getMatchId();
        String teamCode = pointBettngRequestDto.getTeamCode();
        log.info("checkloog2");
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
        );
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
        PointLog pointLog = new PointLog(amount, String.format("betting %s unstarted", teamCode), matchId, point);
        point.update(-amount);
        pointLogRepository.save(pointLog);
        pointLog = pointLogRepository.findByMatchIdAndPoint(matchId, point).orElseThrow(() ->
                new IllegalArgumentException("기록을 찾을 수 없습니다.")
        ); //Query did not return a unique result: 2 results were returned 이미 배팅한 경기에 또다시 배팅하려고 할때

        PointLogResponseDto pointLogResponseDto = new PointLogResponseDto(pointLog);

        return new StatusCodeResponseDto(HttpStatus.CREATED.value(), "point log saved", pointLogResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<?> checkOdds(String matchId, User user) {
        // 한번에 모든 사람들에게 배당금지급하도록 수정
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
//        Point point = pointRepository.findByUser(user).orElseThrow(() ->
//                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
//        );
//        PointLog userPointLog = pointLogRepository.findByMatchIdAndPoint(matchId, point).orElseThrow(() ->
//                new IllegalArgumentException("기록을 찾을 수 없습니다.")
//        );
        String team1Outcome = schedule.getTeam1Outcome();
        String team2Outcome = schedule.getTeam2Outcome();
        String team1Code = schedule.getTeam1Code();
        String team2Code = schedule.getTeam2Code();
//        String[] statusStrings = userPointLog.getStatus().split(" ");
//        if (statusStrings.length > 2) {
//            return new StatusCodeResponseDto(HttpStatus.BAD_REQUEST.value(), "이미 처리된 betting입니다.");
//        }
//        int bettingTeam = Integer.parseInt(userPointLog.getStatus().split(" ")[1]);
        log.info(schedule.getState());
        int count = 0;
        if (schedule.getState().equals("completed")) {
            List<PointLog> pointLogList = pointLogRepository.findAllByMatchId(matchId);
            Map<String, Float> oddsMap = getOdds(schedule, pointLogList);
            for (PointLog pointLog : pointLogList) {
                String[] statusStrings = pointLog.getStatus().split(" ");
                if (statusStrings.length > 2) {
                    continue;
//                    return new StatusCodeResponseDto(HttpStatus.BAD_REQUEST.value(), "이미 처리된 betting입니다.");
                }
                String bettingTeam = statusStrings[1];
                if ((team1Outcome.equals("win") && bettingTeam.equals(team1Code))
                        || (team2Outcome.equals("win") && bettingTeam.equals(team2Code))) {
                    float odds = oddsMap.get(bettingTeam);
                    pointUtil.winPoint(pointLog, odds);
                    count++;
//                    return new StatusCodeResponseDto(HttpStatus.OK.value(), "win point", (int) (amount*odds));
                }
                else {
                    pointUtil.lossPoint(pointLog);
//                    return new StatusCodeResponseDto(HttpStatus.OK.value(), "loss point", userPointLog.getAmount());
                }
            }
            // 0, 1 대신 1, 2로 바뀔가능성 있음

        }
        return new StatusCodeResponseDto(HttpStatus.OK.value(), String.format("%d개의 배당금이 분배되었습니다.", count));
    }

    private Map<String, Float> getOdds(Schedule schedule, List<PointLog> pointLogList) {
        Long betting1 = 0l;
        Long betting2 = 0l;
        String team1Code = schedule.getTeam1Code();
        String team2Code = schedule.getTeam2Code();
        for (PointLog pointLog : pointLogList) {
            int amount = pointLog.getAmount();
            String bettingTeam = pointLog.getStatus().split(" ")[1];
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
