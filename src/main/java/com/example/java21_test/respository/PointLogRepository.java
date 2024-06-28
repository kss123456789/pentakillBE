package com.example.java21_test.respository;

import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import com.example.java21_test.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    Optional<PointLog> findByScheduleAndPoint(Schedule schedule, Point point);
    List<PointLog> findAllBySchedule(Schedule schedule);

    @Query(value = "SELECT * FROM point_log WHERE schedule_match_id IN :scheduleMatchIdList", nativeQuery = true)
    List<PointLog> findAllByScheduleIds(@Param("scheduleMatchIdList") List<String> scheduleMatchIdList);
}
