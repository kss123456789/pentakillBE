package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.LeagueScheduleMapper;
import com.example.java21_test.dto.mapper.ScheduleMapper;
import com.example.java21_test.dto.responseDto.LeagueScheduleResponseDto;
import com.example.java21_test.dto.responseDto.PageResponseScheduleByDateDto;
import com.example.java21_test.entity.Schedule;
import com.example.java21_test.respository.ScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j(topic = "openApi schedule 저장, db조회")
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ApiService apiService;
    private final ScheduleTransactionalService scheduleTransactionalService;

//    api 읽기, 값 저장, 값 가져오기, leagueId 확인
    @Value("${league.ids}")
    private List<String> leagueIdList;

    public void saveLeagueSchedules() {
        log.info("리그스케쥴 업데이트");
        for (String leagueId : leagueIdList) {
            String json = apiService.getScheduleJsonFromApi(leagueId, null);
            List<JsonNode> jsonNodeList = new ArrayList<>();
            getScheduleNodesFromJson(json, leagueId, jsonNodeList);
            saveScheduleFromJson(jsonNodeList);
        }
    }

    public PageResponseScheduleByDateDto<LocalDate, List<Schedule>> getLeagueSchedules(Integer size, Integer page, Integer year, Integer month) {
        // 목표 년월
        String targetYearMonth = getTargetYearMonth(year, month);
        year = Integer.valueOf(targetYearMonth.split("-")[0]);
        month = Integer.valueOf(targetYearMonth.split("-")[1]);
        // 한달전체 일정
        List<Schedule> scheduleList = scheduleRepository.findByStartTimeWithYearAndMonth(targetYearMonth);
        // 날짜별로 그룹
        Map<LocalDate, List<LeagueScheduleResponseDto>> schedulesByDate = getSchedulesGroupedByDate(scheduleList);
        // pagination
        long totalElements = schedulesByDate.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        Map<LocalDate, List<LeagueScheduleResponseDto>> pagedSchedules = getPage(schedulesByDate, page, size);
        return new PageResponseScheduleByDateDto<>(HttpStatus.OK.value(), targetYearMonth + " schedule-pagination 정보입니다.",
                pagedSchedules, year, month, page, totalPages, totalElements, size);
    }

    public String getTargetYearMonth(Integer year, Integer month) {
        String targetYearMonth;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        // 최초접속
        if (year == null && month == null) {
            targetYearMonth = LocalDate.now().format(formatter);
        } else if (year == null || month == null ) {
            throw new IllegalArgumentException("year, month 둘중 하나의 값만 있습니다.");
        } else { // 특정 년월 입력시
            targetYearMonth = LocalDate.of(year, month, 1).format(formatter);
        }
        return targetYearMonth;
    }
    public Map<LocalDate, List<LeagueScheduleResponseDto>> getSchedulesGroupedByDate(List<Schedule> scheduleList) {
        return scheduleList.stream()
                .map(LeagueScheduleMapper::toDto)
                .collect(Collectors.groupingBy(
                        (scheduleResponseDto) -> {
                            String instantString = scheduleResponseDto.getStartTime();
                            return getDateFromInstantString(instantString);
                        },
                        LinkedHashMap::new, // Use LinkedHashMap as the map supplier
                        Collectors.toList()
                ));
    }

    public <K, V> Map<K, V> getPage(Map<K, V> map, int pageNumber, int pageSize) {
        int start = pageNumber * pageSize;
        int end = start + pageSize;
        int currentIndex = 0;

        Map<K, V> page = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (currentIndex >= start && currentIndex < end) {
                page.put(entry.getKey(), entry.getValue());
            }
            currentIndex++;
            if (currentIndex >= end) {
                break;
            }
        }

        return page;
    }

    public LocalDate getDateFromInstantString(String instantString) {
        Instant instant = Instant.parse(instantString);
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
    }

    public void getScheduleNodesFromJson(String json, String leagueId, List<JsonNode> jsonNodeList) {
        log.info("전체 jsonNode에서 scheduleNode 가져오기");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode scheduleNodes = objectMapper.readTree(json).get("data").get("schedule").get("events");
            // newer 확인
            String newer = objectMapper.readTree(json).get("data").get("schedule").get("pages").get("newer").asText();
            jsonNodeList.add(scheduleNodes);
            if (!newer.equals("null")) {
                log.info("추가 페이지 확인!");
                // 재귀로 추가 페이지가 없을 때 까지 불러온다.
                getScheduleNodesFromJson(apiService.getScheduleJsonFromApi(leagueId, newer), leagueId, jsonNodeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리
        }
    }

    public void saveScheduleFromJson(List<JsonNode> jsonNodeList) {
        for (JsonNode jsonNode : jsonNodeList) {
            for (JsonNode scheduleNode : jsonNode) {
                // JSON 데이터에서 필요한 정보 추출, Schedule 객체 생성 저장
                Schedule schedule = ScheduleMapper.toDto(scheduleNode);
                scheduleTransactionalService.saveSchedule(schedule);
            }
        }
    }
}