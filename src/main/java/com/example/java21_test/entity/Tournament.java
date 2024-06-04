package com.example.java21_test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tournaments")
public class Tournament {
    @Id
    private String id;
    private String slug;
    private String startDate;
    private String endDate;

    public Tournament(String id, String slug, String startDate, String endDate) {
        this.id = id;
        this.slug = slug;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
