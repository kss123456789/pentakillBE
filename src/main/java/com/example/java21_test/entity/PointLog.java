package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PointLog extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    private String teamCode;

    @Column(nullable = false)
    private String status; // enum으로 변경가능 -> 현재로서는 불가 status내에 들어있는 정보가 2가지가 되었다... 나눠야 될것 같다.

    @ManyToOne
    @JoinColumn(name = "schedule_matchId", updatable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false, updatable = false)
    private Point point;

    public PointLog(int amount, String status, Point point) {
        this.amount = amount;
        this.teamCode = null;
        this.status = status;
        this.schedule = null;
        this.point = point;
    }

    public PointLog(int amount, String teamCode, String status, Schedule schedule, Point point) {
        this.amount = amount;
        this.teamCode = teamCode;
        this.status = status;
        this.schedule = schedule;
        this.point = point;
    }

    public void update(int amount, String status) {
        this.amount = amount;
        this.status = status;
    }
}
