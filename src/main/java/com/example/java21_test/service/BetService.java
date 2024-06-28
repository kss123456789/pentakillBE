package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.BettingSchedulerMapper;
import com.example.java21_test.dto.responseDto.*;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public void saveTournaments() {
        log.info("최근토너먼트 업데이트");
        for (String leagueId : leagueIdList) {
            String json = apiService.getTournamentJsonFromApi(leagueId);
            tournamentTransactionalService.saveTournamentsFromJson(json);
        }
    }

    public StatusCodeResponseDto<WeeklySchedulesPageResponseDto> getRecentTournamentSchedulesPage(Integer page, UserDetailsImpl userDetails) {
        // 최근 토너먼트 경기일정
        List<Schedule> scheduleList = getRecentTournamentSchedule();
        // 로그인 여부에 맞게 point 가져오기
        Point point = getPoint(userDetails);
        // 전체 일정에서 배팅관련, 예측 값 추가된 그룹값 response 생성
        Map<String, List<BettingScheduleResponseDto>> groupedByBlockName = getScheduleGroup(scheduleList, point);
        List<String> scheduleListKeySet = new ArrayList<>(groupedByBlockName.keySet());
        List<List<BettingScheduleResponseDto>> scheduleListValueSet = new ArrayList<>(groupedByBlockName.values());
        // 현재 주차 확인
        // block에 따라 그룹화된 값중 현재위치
        int currentBlockNameIndex = getCurreentBlockIndex(scheduleListKeySet, scheduleListValueSet);
        if (page == null) {
            page = currentBlockNameIndex;
        }
        int totalPages = scheduleListKeySet.size() - 1;

        WeeklySchedulesPageResponseDto weeklySchedulesPageResponseDto = new WeeklySchedulesPageResponseDto(
                scheduleListValueSet.get(page), scheduleListKeySet, currentBlockNameIndex, page, totalPages);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "최근 토너먼트 일정 조회", weeklySchedulesPageResponseDto);
    }

    public StatusCodeResponseDto<AccuracyResponseDto> getAccuracy() {
        // 최근 토너먼트 경기일정
        List<Schedule> scheduleList = getRecentTournamentSchedule();
        // 최근토너먼트 경기 일정들에서 ai가 이길거라고 예상한 팀이 어느정도로 이겼나 정확도
        float accuracy = getAccuracy(scheduleList);

        AccuracyResponseDto accuracyResponseDto = new AccuracyResponseDto(accuracy);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "이번 시즌의 AI 정확도", accuracyResponseDto);
    }

    private static float getAccuracy(List<Schedule> scheduleList) {
        int completeCount = 0;
        float predictCount = 0f;
        for (Schedule schedule : scheduleList) {
            Probability probability = schedule.getProbability();
            if (schedule.getState().equals("completed")) {
                completeCount++;
                if ((schedule.getTeam1Outcome().equals("win") && probability.getProbability1() > 50) ||
                        (schedule.getTeam2Outcome().equals("win") && probability.getProbability2() > 50)) {
                    predictCount++;
                }
            }
        }
        return completeCount == 0 ? 0 : predictCount / completeCount;
    }

    private static int getCurreentBlockIndex(List<String> scheduleListKeySet, List<List<BettingScheduleResponseDto>> scheduleListValueSet) {
        int curreentBlockIndex = 0;
        for (List<BettingScheduleResponseDto> scheduleListValue : scheduleListValueSet) {
            // 주차별 결과의 시작일이 오늘보다 이전일 경우 current week
            Instant blockStart = Instant.parse(scheduleListValue.getFirst().getStartTime());
            // 현재 시간 가져오기
            Instant instantNow = Instant.now();
            if (blockStart.isBefore(instantNow)) {
                curreentBlockIndex = scheduleListKeySet.indexOf(scheduleListValue.getFirst().getBlockName());
            }
        }
        return curreentBlockIndex;
    }

    private Point getPoint(UserDetailsImpl userDetails) {
        // 로그인 하지 않은 사용자를 위한 default값
        Point point = null;
        // 로그인한 사용자 정보 가져오기
        if (userDetails != null) {
            User user = userDetails.getUser();
            point = pointRepository.findByUser(user).orElseThrow(() ->
                    new BadCredentialsException("존재하지 않는 사용자 입니다.")
            );
        }
        return point;
    }

    private Map<String, List<BettingScheduleResponseDto>> getScheduleGroup(List<Schedule> scheduleList, Point point) {
        Map<String, List<BettingScheduleResponseDto>> groupedByBlockName = new LinkedHashMap<>();
        List<String> scheduleMatchIdList = scheduleList.stream()
                .map(Schedule::getMatchId)
                .collect(Collectors.toList());
        Map<Schedule, List<PointLog>> pointLogMap = pointLogRepository.findAllByScheduleIds(scheduleMatchIdList).stream()
                .collect(Collectors.groupingBy(PointLog::getSchedule));

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

            List<PointLog> pointLogList = pointLogMap.get(schedule);
            if (pointLogList != null && !pointLogList.isEmpty()) {
                // 투표율 구하기
                Map<String, Float> ratioMap = getBettingRatios(schedule, pointLogList);
                ratio1 = ratioMap.get(schedule.getTeam1Code());
                ratio2 = ratioMap.get(schedule.getTeam2Code());
                for (PointLog pointLog : pointLogList) {
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
                    new NoSuchElementException("토너먼트 일정이 없음"));
        }
        String slug = tournament.getSlug().split("_")[0];
        String startDate = tournament.getStartDate();
        String endDate = tournament.getEndDate();

        return scheduleRepository
                .findAllByLeagueSlugAndStartTimeBetweenOrderByStartTimeAsc(slug, startDate, endDate);
    }

    private static Map<String, Float> getBettingRatios(Schedule schedule, List<PointLog> pointLogList) {
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
