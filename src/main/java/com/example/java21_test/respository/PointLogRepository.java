package com.example.java21_test.respository;

import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    Optional<PointLog> findByMatchIdAndPoint(String matchId, Point point);
    List<PointLog> findAllByMatchId(String matchId);
}
