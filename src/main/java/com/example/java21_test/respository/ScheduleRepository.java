package com.example.java21_test.respository;

import com.example.java21_test.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findTop1ByLeagueSlugAndStartTimeAfterOrderByStartTimeAsc(String slug, String startDate);
    List<Schedule> findAllByLeagueSlugAndStartTimeBetweenOrderByStartTimeAsc(String slug, String startDate, String endDate);
    Page<Schedule> findAllByOrderByStartTimeDesc(Pageable pageable);
    Optional<Schedule> findByMatchId(String matchId);

}
