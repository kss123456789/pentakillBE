package com.example.java21_test.dto.responseDto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String nickname;
    private Long replyCount;

    public CommentResponseDto(Long id, String content,
                              LocalDateTime createAt, LocalDateTime modifiedAt, String nickname, Long replyCount) {
        this.id = id;
        this.content = content;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
        this.nickname = nickname;
        this.replyCount = replyCount;
    }
}
