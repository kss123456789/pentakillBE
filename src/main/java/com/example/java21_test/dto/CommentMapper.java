package com.example.java21_test.dto;

import com.example.java21_test.entity.Comment;

import java.time.Instant;

public class CommentMapper {
    public static CommentResponseDto toDto(Comment comment) {
        Long id = comment.getId();
        String content = comment.getContent();
        Instant createAt = comment.getCreatedAt();
        Instant modifiedAt = comment.getModifiedAt();
        String nickname = comment.getUser().getUsername();
        Long replyCount = (long) comment.getReplyList().size();

        return new CommentResponseDto(id, content, createAt, modifiedAt, nickname, replyCount);
    }
}
