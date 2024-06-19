package com.example.java21_test.respository;

import com.example.java21_test.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    Optional<Schedule> findTop1ByLeagueSlugAndStartTimeAfterOrderByStartTimeAsc(String slug, String startDate);
    List<Schedule> findAllByLeagueSlugAndStartTimeBetweenOrderByStartTimeAsc(String slug, String startDate, String endDate);
    Page<Schedule> findAllByOrderByStartTimeDesc(Pageable pageable);
    Optional<Schedule> findByMatchId(String matchId);

//    List<Schedule> findTop5ByTeam1CodeOrTeam2CodeOrderByStartTimeDesc(String teamCode);

//    @Query("SELECT t FROM Schedule t WHERE t.state = :state AND (t.team1Name = :teamName OR t.team2Name = :teamName) ORDER BY t.startTime DESC")
    @Query(value = "SELECT * FROM Schedule t WHERE t.state = :state AND (t.team1Name = :teamName OR t.team2Name = :teamName) ORDER BY start_time DESC LIMIT 5", nativeQuery = true)
    List<Schedule> findTop5ByStateAndTeamName(@Param("state") String state, @Param("teamName") String teamName);
}
