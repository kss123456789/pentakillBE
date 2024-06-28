package com.example.java21_test.dto.responseDto;

import lombok.Getter;

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
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String nickname;
    private String email;

    public PostResponseDto(Long id, String title, String content,
                           Boolean isLike, Long likeCount, Long dislikeCount, Long commentCount, Long views,
                           LocalDateTime createdAt, LocalDateTime modifiedAt, String nickname, String email) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
        this.views = views;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.nickname = nickname;
        this.email = email;

    }
}
