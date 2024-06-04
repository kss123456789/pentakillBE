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

import java.util.List;

@Slf4j(topic = "point 배팅, 포인트분배 등 포인트 관련")
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointUtil pointUtil;

    // point betting 경기가 시작하지 않은걸 확인하고 betting시작
    @Transactional
    public StatusCodeResponseDto pointBetting(PointBettngRequestDto pointBettngRequestDto, User user) {
        int amount = pointBettngRequestDto.getPoint();
        String matchId = pointBettngRequestDto.getMatchId();
        int teamNum = pointBettngRequestDto.getTeamNum();
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
        );
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
        PointLog pointLog = new PointLog(amount, String.format("betting %d", teamNum), matchId, point);
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
        Schedule schedule = scheduleRepository.findByMatchId(matchId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 경기일정입니다.")
        );
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
        );
        PointLog userPointLog = pointLogRepository.findByMatchIdAndPoint(matchId, point).orElseThrow(() ->
                new IllegalArgumentException("기록을 찾을 수 없습니다.")
        );
        String checkTeam1 = schedule.getTeam1Outcome();
        String[] statusStrings = userPointLog.getStatus().split(" ");
        if (statusStrings.length > 2) {
            return new StatusCodeResponseDto(HttpStatus.BAD_REQUEST.value(), "이미 처리된 betting입니다.");
        }
        int bettingTeam = Integer.parseInt(userPointLog.getStatus().split(" ")[1]);
        log.info(schedule.getState());
        if (schedule.getState().equals("completed")) {
            float[] oddsArray = getOdds(matchId);
            // 0, 1 대신 1, 2로 바뀔가능성 있음
            if ((checkTeam1.equals("win") && bettingTeam == 0) || (checkTeam1.equals("loss") && bettingTeam == 1)) {
                float odds = oddsArray[bettingTeam];
                int amount = userPointLog.getAmount();
                pointUtil.winPoint(userPointLog, odds);
                return new StatusCodeResponseDto(HttpStatus.OK.value(), "win point", (int) (amount*odds));
            }
            else {
                pointUtil.lossPoint(userPointLog);
                return new StatusCodeResponseDto(HttpStatus.OK.value(), "loss point", userPointLog.getAmount());
            }
        }
        return new StatusCodeResponseDto(HttpStatus.OK.value(), "not completed");
    }

    private float[] getOdds(String matchId) {
        List<PointLog> pointLogList = pointLogRepository.findAllByMatchId(matchId);
        Long betting1 = 0l;
        Long betting2 = 0l;
        for (PointLog pointLog : pointLogList) {
            int amount = pointLog.getAmount();
            int bettingTeam = Integer.parseInt(pointLog.getStatus().split(" ")[1]);
            if (bettingTeam == 0) {
                betting1 += amount;
            }
            else {
                betting2 += amount;
            }
        }
        float odd1 = (betting1 + betting2)/betting1;
        float odd2 = (betting1 + betting2)/betting2;
        float[] floats = {odd1, odd2};
        return floats;
    }
}
