package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PointLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    private String status; // enum으로 변경가능
    //one to one?
    private String matchId;

    @ManyToOne
    @JoinColumn(name = "point_id")
    private Point point;

    public PointLog(int amount, String status, Point point) {
        this.amount = amount;
        this.status = status;
        this.matchId = null;
        this.point = point;
    }

    public PointLog(int amount, String status, String matchId, Point point) {
        this.amount = amount;
        this.status = status;
        this.matchId = matchId;
        this.point = point;
    }

    public void update(int amount, String status) {
        this.amount = amount;
        this.status = status;
    }
}
