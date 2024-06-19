package com.example.java21_test.service;


import com.example.java21_test.dto.requestDto.ProbabilityRequestDto;
import com.example.java21_test.util.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "openApi 조회")
@Service
@RequiredArgsConstructor
public class ApiService {
    private final RestTemplateUtil restTemplateUtil;

    public String getTournamentJsonFromApi(String leagueId) {
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

        return result.getBody();
    }

    public String getScheduleJsonFromApi(String leagueId, String newer) {
        log.info("특정리그 schedule 가져오기 from api");

        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://esports-api.lolesports.com")
                .path("/persisted/gw/getSchedule")
                .queryParam("hl", "ko-KR")
                .queryParam("leagueId", leagueId)
                .queryParam("pageToken", newer)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getDataFromAPI(targetUrl);

        return result.getBody();
    }

    public String getProbabilityJsonFromDS(ProbabilityRequestDto probabilityRequestDto) {
        URI targetUrl = UriComponentsBuilder
                .fromUriString("localhost:5000")
                .path("/probability")
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getProbabilityFromDS(targetUrl, probabilityRequestDto);

        return result.getBody();
    }

    public String getTeamDataJsonFromApi(String teamCode) {
        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://esports-api.lolesports.com")
                .path("/persisted/gw/getEventDetails")
                .queryParam("hl", "ko-KR")
                .queryParam("id", teamCode)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getDataFromAPI(targetUrl);

        return result.getBody();
    }

    public String getGameDataJsonFromApi(String gameId) {
        URI targetUrl = UriComponentsBuilder
                .fromUriString("https://feed.lolesports.com")
                .path("/livestats/v1/window/" + gameId)
                .build()
                .encode(StandardCharsets.UTF_8) //인코딩
                .toUri();

        ResponseEntity<String> result = restTemplateUtil.getDataFromAPI(targetUrl);

        return result.getBody();
    }
}
