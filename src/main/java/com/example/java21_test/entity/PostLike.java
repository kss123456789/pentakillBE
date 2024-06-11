package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isLike; // null가능

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    public PostLike(User user, Post post, Boolean isLike) {
        this.user = user;
        this.post = post;
        this.isLike = isLike;
    }

    public void update(Boolean isLike) {
        this.isLike = isLike;
    }
}
