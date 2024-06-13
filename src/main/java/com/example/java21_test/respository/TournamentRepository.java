package com.example.java21_test.respository;

import com.example.java21_test.entity.Schedule;
import com.example.java21_test.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, String> {

    Optional<Tournament> findById(String id);

    //오늘기준 tournament가 끝나지 않은 값들 중 가장 최근 값1개
    Optional<Tournament> findTop1ByEndDateAfterOrderByStartDateAsc(String localDateNow);
}
