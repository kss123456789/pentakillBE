package com.example.java21_test.respository;

import com.example.java21_test.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findAllByOrderByStartTimeDesc(Pageable pageable);
//    List<Schedule> findAllByLeagueSlugAndStartTimeAfterOOrderByStartTimeDesc(String slug, String startTime);
    Optional<Schedule> findByMatchId(String matchId);

    List<Schedule> findAllByLeagueSlugAndStartTimeAfterAndBlockName(String slug, String startDate, String blockName);

}
