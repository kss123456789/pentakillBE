package com.example.java21_test.dto.responseDto;

import com.example.java21_test.entity.Reply;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String nickname;

    public ReplyResponseDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.createAt = reply.getCreatedAt();
        this.modifiedAt = reply.getModifiedAt();
        this.nickname = reply.getUser().getUsername();
    }
}
