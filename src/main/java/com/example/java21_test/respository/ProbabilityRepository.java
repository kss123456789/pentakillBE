package com.example.java21_test.respository;

import com.example.java21_test.entity.Probability;
import com.example.java21_test.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProbabilityRepository extends JpaRepository<Probability, Long> {
    Optional<Probability> findBySchedule(Schedule schedule);
}
