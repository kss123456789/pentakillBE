package com.example.java21_test.dto.mapper;

import com.example.java21_test.entity.Schedule;
import com.fasterxml.jackson.databind.JsonNode;

public class ScheduleMapper {
    public static Schedule toDto(JsonNode rootNode) {
        String matchId = rootNode.get("match").get("id").asText();

        String startTime = rootNode.get("startTime").asText();
        String state = rootNode.get("state").asText();
        String type = rootNode.get("type").asText();
        String blockName = rootNode.get("blockName").asText();
        String leagueName = rootNode.get("league").get("name").asText();
        String leagueSlug = rootNode.get("league").get("slug").asText();

        JsonNode teamsNode = rootNode.get("match").get("teams");

        String team1Name = teamsNode.get(0).get("name").asText();
        String team1Code = teamsNode.get(0).get("code").asText();
        String team1Image =  httpToHttps(teamsNode.get(0).get("image").asText());
        // result, record가 null인 경우 발견
        String team1Outcome = null;
        int team1GameWins = 0;
        int team1RecordWins = 0;
        int team1RecordLosses = 0;
        if (!teamsNode.get(0).get("result").isNull()) {
            team1Outcome = teamsNode.get(0).get("result").get("outcome").asText();
            team1GameWins = teamsNode.get(0).get("result").get("gameWins").asInt();
            team1RecordWins = teamsNode.get(0).get("record").get("wins").asInt();
            team1RecordLosses = teamsNode.get(0).get("record").get("losses").asInt();
        }

        String team2Name = teamsNode.get(1).get("name").asText();
        String team2Code = teamsNode.get(1).get("code").asText();
        String team2Image = httpToHttps(teamsNode.get(1).get("image").asText());
        String team2Outcome = null;
        int team2GameWins = 0;
        int team2RecordWins = 0;
        int team2RecordLosses = 0;
        if (!teamsNode.get(1).get("result").isNull()) {
            team2Outcome = teamsNode.get(1).get("result").get("outcome").asText();
            team2GameWins = teamsNode.get(1).get("result").get("gameWins").asInt();
            team2RecordWins = teamsNode.get(1).get("record").get("wins").asInt();
            team2RecordLosses = teamsNode.get(1).get("record").get("losses").asInt();
        }

        String matchStrategyType = rootNode.get("match").get("strategy").get("type").asText();
        int matchStrategyCount = rootNode.get("match").get("strategy").get("count").asInt();

        return new Schedule(startTime, state, type, blockName, leagueName, leagueSlug, matchId,
                team1Name, team1Code, team1Image, team1Outcome, team1GameWins, team1RecordWins, team1RecordLosses,
                team2Name, team2Code, team2Image, team2Outcome, team2GameWins, team2RecordWins, team2RecordLosses,
                matchStrategyType, matchStrategyCount);
    }

    private static String httpToHttps(String url) {
        if (url.startsWith("http")) {
            return url.replace("http://", "https://");
        }
        return url;
    }
}
