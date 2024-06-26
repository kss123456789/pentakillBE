package com.example.java21_test.controller;

import com.example.java21_test.dto.requestDto.CommentRequestDto;
import com.example.java21_test.dto.responseDto.CommentResponseDto;
import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/back/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody @Valid CommentRequestDto commentRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<CommentResponseDto> responseDto =
                commentService.createComment(postId, commentRequestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @GetMapping()
    public ResponseEntity<?> getCommentPage(@PathVariable Long postId,
                                            @RequestParam Integer size, @RequestParam Integer page) {
        PageResponseDto<CommentResponseDto> pageResponseDto = commentService.getCommentPage(postId, size, page);
        return ResponseEntity.ok()
                .body(pageResponseDto);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @RequestBody @Valid CommentRequestDto commentRequestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<CommentResponseDto> responseDto =
                commentService.updateComment(postId, commentId, commentRequestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<Void> responseDto = commentService.deleteComment(postId, commentId, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }
}
