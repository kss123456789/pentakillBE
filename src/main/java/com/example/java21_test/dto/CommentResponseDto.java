package com.example.java21_test.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private Instant createAt;
    private Instant modifiedAt;
    private String nickname;
    private Long replyCount;

    public CommentResponseDto(Long id, String content,
                           Instant createAt, Instant modifiedAt, String nickname, Long replyCount) {
        this.id = id;
        this.content = content;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
        this.nickname = nickname;
        this.replyCount = replyCount;
    }
}
