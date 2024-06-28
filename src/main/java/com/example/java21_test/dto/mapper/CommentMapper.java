package com.example.java21_test.dto.mapper;

import com.example.java21_test.dto.responseDto.CommentResponseDto;
import com.example.java21_test.entity.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentResponseDto toDto(Comment comment) {
        Long id = comment.getId();
        String content = comment.getContent();
        LocalDateTime createAt = comment.getCreatedAt();
        LocalDateTime modifiedAt = comment.getModifiedAt();
        String nickname = comment.getUser().getUsername();
        Long replyCount = (long) comment.getReplyList().size();
        String email = comment.getUser().getEmail();

        return new CommentResponseDto(id, content, createAt, modifiedAt, nickname, replyCount, email);
    }
}
