package com.example.java21_test.respository;

import com.example.java21_test.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    @Query(value = "SELECT * FROM schedule t " +
            "WHERE DATE_FORMAT(CONVERT_TZ(STR_TO_DATE(t.start_time, '%Y-%m-%dT%H:%i:%sZ'), '+00:00', @@session.time_zone), '%Y-%m') = :targetYearMonth " +
            "ORDER BY start_time ASC", nativeQuery = true)
    List<Schedule> findByStartTimeWithYearAndMonth(@Param("targetYearMonth") String targetYearMonth);
    List<Schedule> findAllByLeagueSlugAndStartTimeBetweenOrderByStartTimeAsc(String slug, String startDate, String endDate);
    Optional<Schedule> findByMatchId(String matchId);

//    List<Schedule> findTop5ByTeam1CodeOrTeam2CodeOrderByStartTimeDesc(String teamCode);

//    @Query("SELECT t FROM Schedule t WHERE t.state = :state AND (t.team1Name = :teamName OR t.team2Name = :teamName) ORDER BY t.startTime DESC")
    @Query(value = "SELECT * FROM schedule t " +
            "WHERE t.state = :state AND (t.team1Name = :teamName OR t.team2Name = :teamName) " +
            "ORDER BY start_time DESC LIMIT 5", nativeQuery = true)
    List<Schedule> findTop5ByStateAndTeamName(@Param("state") String state, @Param("teamName") String teamName);

    @Query(value = "SELECT * FROM schedule t " +
            "WHERE t.league_slug = :slug " +
            "AND DATE_FORMAT(CONVERT_TZ(STR_TO_DATE(t.start_time, '%Y-%m-%dT%H:%i:%sZ'), '+00:00', @@session.time_zone), '%Y-%m-%d') = :localDateNow " +
            "ORDER BY start_time DESC LIMIT 5", nativeQuery = true)
    List<Schedule> findAllByLeagueSlugAndStartDate(@Param("slug") String slug, @Param("localDateNow") String localDateNow);
}
