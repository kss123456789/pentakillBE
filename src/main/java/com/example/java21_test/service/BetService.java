package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.BettingSchedulerMapper;
import com.example.java21_test.dto.responseDto.BettingScheduleResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.dto.responseDto.WeeklySchedulesResponseDto;
import com.example.java21_test.entity.*;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import com.example.java21_test.respository.ScheduleRepository;
import com.example.java21_test.respository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j(topic = "openApi tournaments 저장, db조회, bet system")
@Service
@RequiredArgsConstructor
public class BetService {
    private final TournamentRepository tournamentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final ApiService apiService;
    private final TournamentTransactionalService tournamentTransactionalService;

    @Value("${majorLeague.ids}")
    private List<String> leagueIdList;

    //최근 토너먼트 일정이 추가되었는가 매일 1시에 확인 // 업데이트 나중에 한곳에 모아서 처리
    @Scheduled(cron = "0 0 1 * * ?")
//    @PostConstruct
    public void saveTournaments() {
        log.info("최근토너먼트 업데이트");
        for (String leagueId : leagueIdList) {
            String json = apiService.getTournamentJsonFromApi(leagueId);
            tournamentTransactionalService.saveTournamentsFromJson(json);
        }
    }

    public StatusCodeResponseDto<WeeklySchedulesResponseDto> getRecentTournamentSchedules(UserDetailsImpl userDetails) {
        // 최근 토너먼트 경기일정
        List<Schedule> scheduleList = getRecentTournamentSchedule();
        // 로그인 여부에 맞게 point 가져오기
        Point point = getPoint(userDetails);
        // 전체 일정에서 배팅관련, 예측 값 추가된 그룹값 response 생성
        Map<String, List<BettingScheduleResponseDto>> groupedByBlockName = getScheduleGroup(scheduleList, point);
        //
        List<String> scheduleListKeySet = new ArrayList<>(groupedByBlockName.keySet());
        List<List<BettingScheduleResponseDto>> scheduleListValueSet = new ArrayList<>(groupedByBlockName.values());
        // 현재 주차 확인
        // block에 따라 그룹화된 값중 현재위치
        int curreentBlockIndex = 0;
        for (List<BettingScheduleResponseDto> scheduleListValue : scheduleListValueSet) {
            if (curreentBlockIndex == 0) {
                // 주차별 결과의 시작일이 오늘보다 이전일 경우 current week
                Instant blockStart = Instant.parse(scheduleListValue.getFirst().getStartTime());
                Instant blockEnd = Instant.parse(scheduleListValue.getLast().getStartTime());
                // 나중에 페이지네이션으로 수정할때 생각해볼것
//                if (blockEnd.isBefore(instantNow)) {
//                    curreentBlockIndex++;
//                }
                // 현재 시간 가져오기
                Instant instantNow = Instant.now();
                if (blockStart.isBefore(instantNow) && blockEnd.isAfter(instantNow)) {
                    curreentBlockIndex = scheduleListKeySet.indexOf(scheduleListValue.getFirst().getBlockName());
                }
            }
        }
        int totalIndex = scheduleListKeySet.size() - 1;

        WeeklySchedulesResponseDto weeklySchedulesResponseDto = new WeeklySchedulesResponseDto(scheduleListValueSet, scheduleListKeySet,
                curreentBlockIndex, totalIndex);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "SUCCESS", weeklySchedulesResponseDto);
    }

    public Point getPoint(UserDetailsImpl userDetails) {
        // 로그인 하지 않은 사용자를 위한 default값
        Point point = null;
        // 로그인한 사용자 정보 가져오기
        if (userDetails != null) {
            User user = userDetails.getUser();
            point = pointRepository.findByUser(user).orElseThrow(() ->
                    new IllegalArgumentException("포인트를 찾을 수 없습니다.")
            );
        }
        return point;
    }

    public Map<String, List<BettingScheduleResponseDto>> getScheduleGroup(List<Schedule> scheduleList, Point point) {
        Map<String, List<BettingScheduleResponseDto>> groupedByBlockName = new LinkedHashMap<>();
        for (Schedule schedule: scheduleList) {
            String blockName = schedule.getBlockName();
            // 같은 blockName끼리 묶어서 관리
            if (!groupedByBlockName.containsKey(blockName)) {
                groupedByBlockName.put(blockName, new ArrayList<>());
            }
            // default response
            boolean betting = false;
            int amount = 0;
            String teamCode = null;
            String status = null;
            float ratio1 = 0f;
            float ratio2 = 0f;
            // 예측된 확률 가져오기
            Probability probability = schedule.getProbability();
            double probability1 = 0d;
            double probability2 = 0d;
            if (probability != null) {
                probability1 = probability.getProbability1();
                probability2 = probability.getProbability2();
            }
            List<PointLog> pointLogList = pointLogRepository.findAllBySchedule(schedule);
            if (!pointLogList.isEmpty()) {
                for (PointLog pointLog : pointLogList) {
                    // 투표율 구하기
                    Map<String, Float> ratioMap = getBettingRatios(schedule, pointLogList);
                    ratio1 = ratioMap.get(schedule.getTeam1Code());
                    ratio2 = ratioMap.get(schedule.getTeam2Code());
                    // 접속자의 투표확인
                    if (pointLog.getPoint().equals(point)) {
                        betting = true;
                        amount = pointLog.getAmount();
                        teamCode = pointLog.getTeamCode();
                        status = pointLog.getStatus();
                    }
                }
            }
            // dto 변환
            groupedByBlockName.get(blockName)
                    .add(BettingSchedulerMapper.toDto(schedule, betting, amount, teamCode, status,
                            ratio1, ratio2, probability1, probability2));
        }
        return groupedByBlockName;
    }

    public List<Schedule> getRecentTournamentSchedule() {
        // 오늘 날짜
        String localDateNow = LocalDate.now().toString();
        // tournament에서 가장 최근 일정을 가져온다
        Tournament tournament = tournamentRepository.findTop1ByEndDateAfterOrderByStartDateAsc(localDateNow).orElse(null);
        if (tournament == null) { //오늘기준 이전 토너먼트는 끝났지만 아직 새로운 토너먼트 일정이 없는 경우
            // 여기서도 없으면 이건 500 서버에러로 처리 가장 날짜가 늦은(최근에 진행된) 토너먼트
            tournament = tournamentRepository.findTopByOrderByStartDateDesc().orElseThrow(() ->
                    new NullPointerException("토너먼트 일정이 없음"));
        }
        String slug = tournament.getSlug().split("_")[0];
        String startDate = tournament.getStartDate();
        String endDate = tournament.getEndDate();

        return scheduleRepository
                .findAllByLeagueSlugAndStartTimeBetweenOrderByStartTimeAsc(slug, startDate, endDate);
    }

    public Map<String, Float> getBettingRatios(Schedule schedule, List<PointLog> pointLogList) {
        int betting1 = 0;
        int betting2 = 0;
        int total = 0;
        float ratio1 = 0;
        float ratio2 = 0;
        String team1Code = schedule.getTeam1Code();
        String team2Code = schedule.getTeam2Code();
        for (PointLog pointLog : pointLogList) {
            String bettingTeam = pointLog.getTeamCode();
            if (bettingTeam.equals(team1Code)) {
                betting1 += 1;
            }
            else {
                betting2 += 1;
            }
            total += 1;
        }
        if (total != 0) {
            ratio1 = betting1 / (float) total;
            ratio2 = betting2 / (float) total;
        }
        Map<String, Float> ratioMap = new HashMap<>();
        ratioMap.put(team1Code, ratio1);
        ratioMap.put(team2Code, ratio2);
        return ratioMap;
    }
}
