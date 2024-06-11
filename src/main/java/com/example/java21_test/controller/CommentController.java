package com.example.java21_test.controller;

import com.example.java21_test.dto.*;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    public StatusCodeResponseDto<CommentResponseDto> createComment(@PathVariable Long postId,
                                                                   @RequestBody CommentRequestDto commentRequestDto,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.createComment(postId, commentRequestDto, userDetails.getUser());
    }

    @GetMapping()
    public PageResponseDto<CommentResponseDto> getCommentPage(@PathVariable Long postId,
                                                           Integer size, Integer page) {
        return commentService.getCommentPage(postId, size, page);
    }

    @PutMapping("/{commentId}")
    public StatusCodeResponseDto<CommentResponseDto> updateComment(@PathVariable Long postId,
                                                                @PathVariable Long commentId,
                                                                @RequestBody CommentRequestDto commentRequestDto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(postId, commentId, commentRequestDto, userDetails.getUser());
    }

    @DeleteMapping("/{commentId}")
    public StatusCodeResponseDto<Void> deleteComment(@PathVariable Long postId,
                                                  @PathVariable Long commentId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(postId, commentId, userDetails.getUser());
    }
}
