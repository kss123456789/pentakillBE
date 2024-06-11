package com.example.java21_test.dto;

import com.example.java21_test.entity.Reply;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ReplyResponseDto {
    private Long id;
    private String content;
    private Instant createAt;
    private Instant modifiedAt;
    private String nickname;

    public ReplyResponseDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.createAt = reply.getCreatedAt();
        this.modifiedAt = reply.getModifiedAt();
        this.nickname = reply.getUser().getUsername();
    }
}
