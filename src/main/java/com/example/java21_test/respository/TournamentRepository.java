package com.example.java21_test.respository;

import com.example.java21_test.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, String> {
    //오늘기준 tournament가 끝나지 않은 값들 중 가장 최근 값1개
    Optional<Tournament> findTop1ByEndDateAfterOrderByStartDateAsc(String localDateNow);
}
