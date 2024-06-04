package com.example.java21_test.respository;

import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);
}
