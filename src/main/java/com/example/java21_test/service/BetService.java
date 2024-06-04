package com.example.java21_test.service;

import com.example.java21_test.dto.LeagueScheduleMapper;
import com.example.java21_test.dto.LeagueScheduleResponseDto;
import com.example.java21_test.dto.RecentWeeklySchedulesResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.entity.Tournament;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "openApi tournaments 저장, db조회, bet system")
@Service
@RequiredArgsConstructor
public class BetService {

    private final RestTemplateUtil restTemplateUtil;
    private final TournamentRepository tournamentRepository;
    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;

    public StatusCodeResponseDto saveTournaments() {
        log.info("최근토너먼트 업데이트");
        List<String> leagueIdList = new ArrayList<>();
        leagueIdList.add("98767975604431411"); //world
        leagueIdList.add("98767991325878492"); //msi
        leagueIdList.add("98767991310872058"); //lck

        for (String leagueId : leagueIdList) {
            saveTournamentsFromApi(leagueId);
        }

        return new StatusCodeResponseDto(HttpStatus.CREATED.value(), "recent tournament saved", null);
    }

    public StatusCodeResponseDto getRecentTournamentSchedules() {
        // 현재 시간 가져오기
        Instant instantNow = Instant.now();
        String nowString = instantNow.toString();
        // tournament에서 가장 최근 일정을 가져온다
        Tournament tournament = tournamentRepository.findTop1ByEndDateAfterOrderByStartDateAsc(nowString).orElse(null);
        if (tournament == null) {
            // 비어 있다면 적절한 응답을 반환
            return new StatusCodeResponseDto(HttpStatus.NOT_FOUND.value(), "No schedules found for the league", null);
        }

        String slug = tournament.getSlug().split("_")[0];
        String startDate = tournament.getStartDate();
        List<List<LeagueScheduleResponseDto>> groupedScheduleDto = new ArrayList<>();

        // 각 주차별 값을 가져와서 같은 list에 넣어서 responseDto 생성
        int weekNum = 1;
        int curreentWeek = 0;
        // while 써서 전체 탐색후 필요한 주차의 값만을 출력 //출력 값에 순서 이상 있는것 같음
        while (true) {
            String blockName = String.format("%d주 차", weekNum);
            List<Schedule> newScheduleList = scheduleRepository.findAllByLeagueSlugAndStartTimeAfterAndBlockNameOrderByStartTimeDesc(slug, startDate, blockName);
            // 주차별 결과가 나오지 않는 것을 확인후 break
            if (newScheduleList.isEmpty()) {
                break;
            }
            if (curreentWeek == 0) {
                // 주차별 결과의 시작일이 오늘보다 이전일 경우 current week
                Instant weekStart = Instant.parse(newScheduleList.getFirst().getStartTime());
                Instant weekEnd = Instant.parse(newScheduleList.getLast().getStartTime());
                if (weekStart.isBefore(instantNow) && weekEnd.isAfter(instantNow)) {
                    curreentWeek = weekNum - 1;
                }
            }

            // schdule dto 변환 후 최종 리스트에 추가
            List<LeagueScheduleResponseDto> newScheduleResponseList = newScheduleList.stream().map(LeagueScheduleMapper::toDto).toList();
            groupedScheduleDto.add(newScheduleResponseList);
            weekNum++;
        }

        RecentWeeklySchedulesResponseDto recentWeeklySchedulesResponseDto = new RecentWeeklySchedulesResponseDto(groupedScheduleDto, curreentWeek, weekNum - 2);

        return new StatusCodeResponseDto(HttpStatus.OK.value(), "SUCCESS", recentWeeklySchedulesResponseDto);
    }

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
}
