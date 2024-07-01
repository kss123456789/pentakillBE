package com.example.java21_test.respository;

import com.example.java21_test.entity.SseNotice;
import com.example.java21_test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SseNoticeRepository extends JpaRepository<SseNotice, Long> {
    List<SseNotice> findAllByUser(User user);
}
