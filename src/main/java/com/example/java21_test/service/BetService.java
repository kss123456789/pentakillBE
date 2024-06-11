package com.example.java21_test.service;

import com.example.java21_test.dto.BettingScheduleResponseDto;
import com.example.java21_test.dto.BettingSchedulerMapper;
import com.example.java21_test.dto.RecentWeeklySchedulesResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.entity.*;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import com.example.java21_test.respository.ScheduleRepository;
import com.example.java21_test.respository.TournamentRepository;
import com.example.java21_test.util.RestTemplateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j(topic = "openApi tournaments 저장, db조회, bet system")
@Service
@RequiredArgsConstructor
public class BetService {

    private final RestTemplateUtil restTemplateUtil;
    private final TournamentRepository tournamentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    public StatusCodeResponseDto<Void> saveTournaments() {
        log.info("최근토너먼트 업데이트");
        List<String> leagueIdList = new ArrayList<>();
        leagueIdList.add("98767975604431411"); //world
        leagueIdList.add("98767991325878492"); //msi
        leagueIdList.add("98767991310872058"); //lck

        for (String leagueId : leagueIdList) {
            saveTournamentsFromApi(leagueId);
        }

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "recent tournament saved");
    }

    public StatusCodeResponseDto<RecentWeeklySchedulesResponseDto> getRecentTournamentSchedules(UserDetailsImpl userDetails) {
        // user가 배팅했는지 아닌지 어떻게 전해주지...?
        // 1. user가 배팅한 모든 경기 리스트 전해주기
        // 2. 각 schedule에 하위에 betting true false같은 항목을 주기
        // 2번이 프론트 입장에서 더 좋을 것으로 예상... 그렇다면 이걸또 언제 넣어주냐...

        // 현재 시간 가져오기
        Instant instantNow = Instant.now();
        String nowString = instantNow.toString();
        // tournament에서 가장 최근 일정을 가져온다
        Tournament tournament = tournamentRepository.findTop1ByEndDateAfterOrderByStartDateAsc(nowString).orElse(null);
        if (tournament == null) {
            // 비어 있다면 적절한 응답을 반환
            return new StatusCodeResponseDto<>(HttpStatus.NOT_FOUND.value(), "No schedules found for the league");
        }

        String slug = tournament.getSlug().split("_")[0];
        String startDate = tournament.getStartDate();
//        List<List<LeagueScheduleResponseDto>> groupedScheduleDto = new ArrayList<>();

        // 각 주차별 값을 가져와서 같은 list에 넣어서 responseDto 생성
//        int weekNum = 1;
        int curreentBlockIndex = 0;
        // Map오로 변환되는 과정에서 정렬이 흐트러진다. 그렇다면 그냥 for문을 통해서 작성하는 쪽이 더 낫겠다...
        // 로그인시에 유저데이터 받아와서 포인트를 걸었는지 아닌지 확인 findbymatchidandUser
        List<Schedule> scheduleList = scheduleRepository.findAllByLeagueSlugAndStartTimeAfterOrderByStartTimeAsc(slug, startDate);
        Map<String, List<BettingScheduleResponseDto>> groupedByBlockName = new LinkedHashMap<>();
        Point point = null;
        List<PointLog> pointLogList = null;
        if (userDetails != null) {
            User user = userDetails.getUser();
            point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트를 찾을 수 없습니다.")
            );
            // 기존: 현재 접속자의 포인트로그전체를 가져옴 -> 수정: 모든 투표자의 포인트 로그를 가져올 필요가 생김
            // 기존에도 접속자의 모든포인트로그중 matchid가 같은걸 찾아서 했으니 대신 같은 matchid로 가져와서 접속자의 로그를 찾는게 나을지도?
            // 문제... 요청을 너무 자주 하게 되는데... 흠...
        }
        for (Schedule schedule: scheduleList) {
            String blockName = schedule.getBlockName();
            // default response 나중에 builder로 바꾸거나 하게 되면 response에서 default로 되도록하자
            boolean betting = false;
            int amount = 0;
            String teamCode = null;
            String status = null;
            float ratio1 = 0;
            float ratio2 = 0;

            pointLogList = pointLogRepository.findAllBySchedule(schedule);
            if (pointLogList != null) {
                for (PointLog pointLog : pointLogList) {
                    // 투표율 구하기
                    Map<String, Float> ratioMap = getBettingRatios(schedule, pointLogList);
                    ratio1 = ratioMap.get(schedule.getTeam1Code());
                    ratio2 = ratioMap.get(schedule.getTeam2Code());
                    // 접속자의 투표확인
                    if (pointLog.getPoint().equals(point)) {
                        betting = true;
                        amount = pointLog.getAmount();
                        teamCode = pointLog.getStatus().split(" ")[1];
                        status = pointLog.getStatus().split(" ")[2];
                    }
                }
            }
            // 같은 blockName끼리 묶어서 관리
            if (!groupedByBlockName.containsKey(blockName)) {
                groupedByBlockName.put(blockName, new ArrayList<>());
            }
            groupedByBlockName.get(blockName).add(BettingSchedulerMapper.toDto(schedule, betting, amount, teamCode, status, ratio1, ratio2));
        }
        List<String> scheduleListKeySet = new ArrayList<>(groupedByBlockName.keySet());
        List<List<BettingScheduleResponseDto>> scheduleListValueSet = new ArrayList<>(groupedByBlockName.values());
        // 현재 주차 확인
        for (List<BettingScheduleResponseDto> scheduleListValue : scheduleListValueSet) {
            if (curreentBlockIndex == 0) {
                // 주차별 결과의 시작일이 오늘보다 이전일 경우 current week
                Instant blockStart = Instant.parse(scheduleListValue.getFirst().getStartTime());
                Instant blockEnd = Instant.parse(scheduleListValue.getLast().getStartTime());
                if (blockStart.isBefore(instantNow) && blockEnd.isAfter(instantNow)) {
                    curreentBlockIndex = scheduleListKeySet.indexOf(scheduleListValue.getFirst().getBlockName());
                }
            }
        }
        int totalIndex = scheduleListKeySet.size() - 1;

        RecentWeeklySchedulesResponseDto recentWeeklySchedulesResponseDto = new RecentWeeklySchedulesResponseDto(scheduleListValueSet, scheduleListKeySet, curreentBlockIndex, totalIndex);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "SUCCESS", recentWeeklySchedulesResponseDto);
    }

    @Transactional
    public void saveTournamentsFromApi(String leagueId) {
        log.info("특정리그 tournaments 가져오기 from api");

        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://esports-api.lolesports.com")
                .path("/persisted/gw/getTournamentsForLeague")
                .queryParam("hl", "ko-KR")
                .queryParam("leagueId", leagueId)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getDataFromAPI(targetUrl);

        saveTournamentsFromJson(result.getBody());
    }

    @Transactional
    public void saveTournamentsFromJson(String json) {
        log.info("json 문자열을 Tournament로 변환");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode latestTournaments = objectMapper.readTree(json).get("data").get("leagues").get(0).get("tournaments").get(0);
            // JSON 데이터에서 필요한 정보 추출
            String id = latestTournaments.get("id").asText();
            // 중복확인
            Tournament checkTournament = tournamentRepository.findById(id).orElse(null);

            if (checkTournament == null) { // 중복 값이 없는 경우 저장
                String slug = latestTournaments.get("slug").asText();
                String startDate = latestTournaments.get("startDate").asText();
                String endDate = latestTournaments.get("endDate").asText();

                Tournament tournament = new Tournament(id, slug, startDate, endDate);
                tournamentRepository.save(tournament);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
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
            String bettingTeam = pointLog.getStatus().split(" ")[1];
            if (bettingTeam.equals(team1Code)) {
                betting1 += 1;
            }
            else {
                betting2 += 1;
            }
            total += 1;

            log.info(String.format("%d %d %d", betting1, betting2, total));
        }
        log.info(String.format("%d %d %d", betting1, betting2, total));
        if (total != 0) {
            ratio1 = betting1 / (float) total;
            ratio2 = betting2 / (float) total;
            log.info(String.format("%f %f", ratio1, ratio2));
        }
        Map<String, Float> ratioMap = new HashMap<>();
        ratioMap.put(team1Code, ratio1);
        ratioMap.put(team2Code, ratio2);
        return ratioMap;
    }
}
