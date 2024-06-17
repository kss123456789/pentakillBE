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
    private float probability1;

    @Column(nullable = false)
    private float probability2;

    @OneToOne
    @JoinColumn(name = "schedule_matchId", nullable = false)
    private Schedule schedule;
}
