package com.example.java21_test.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reply extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, updatable = false)
    private Comment comment;

    public Reply(String content, User user, Comment comment) {
        this.content = content;
        this.user = user;
        this.comment = comment;
    }

    public void update(String content) {
        this.content = content;
    }
}
