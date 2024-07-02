package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SseNotice extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;
    @Column
    private String team1;
    @Column
    private String team2;

    private int point = 0;
    private String outcome;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public SseNotice(String type, String team1, String team2, User user) {
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
        this.user = user;
    }
    public SseNotice(String type, String team1, String team2, PointLog pointLog) {
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
        this.point = pointLog.getAmount();
        this.outcome = pointLog.getStatus();
        this.user = pointLog.getPoint().getUser();
    }

    public SseNotice(String type, PointLog pointLog) {
        this.type = type;
        this.point = pointLog.getAmount();
        this.user = pointLog.getPoint().getUser();
    }
}
