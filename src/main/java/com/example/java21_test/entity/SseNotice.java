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
    @Column(nullable = false)
    private String team1;
    @Column(nullable = false)
    private String team2;

    private int point = 0;
    private String outcome = "";

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public SseNotice(String type, String team1, String team2) {
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
    }
    public SseNotice(String type, String team1, String team2, int point, String outcome) {
        this.type = type;
        this.team1 = team1;
        this.team2 = team2;
        this.point = point;
        this.outcome = outcome;
    }
}
