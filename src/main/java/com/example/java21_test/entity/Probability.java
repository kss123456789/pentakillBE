package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Probability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double probability1;

    @Column(nullable = false)
    private double probability2;

    @OneToOne
    @JoinColumn(name = "schedule_matchId", nullable = false)
    private Schedule schedule;

    public Probability(double probability1, double probability2, Schedule schedule) {
        this.probability1 = probability1;
        this.probability2 = probability2;
        this.schedule = schedule;
    }

    public void update(double probability1, double probability2) {
        this.probability1 = probability1;
        this.probability2 = probability2;
    }
}
