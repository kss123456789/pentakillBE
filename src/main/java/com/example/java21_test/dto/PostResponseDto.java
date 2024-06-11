package com.example.java21_test.dto;

import com.example.java21_test.entity.Post;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Boolean isLike;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Long views;
    private Instant createAt;
    private Instant modifiedAt;
    private String nickname;

    public PostResponseDto(Long id, String title, String content,
                           Boolean isLike, Long likeCount, Long dislikeCount, Long commentCount, Long views,
                           Instant createAt, Instant modifiedAt, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
        this.views = views;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
        this.nickname = nickname;

    }
}
