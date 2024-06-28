package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.requestDto.ReplyRequestDto;
import com.example.java21_test.dto.responseDto.ReplyResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/back/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping()
    public ResponseEntity<?> createReply(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @RequestBody @Valid ReplyRequestDto replyRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<ReplyResponseDto> responseDto =
                replyService.createReply(postId, commentId, replyRequestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping()
    public ResponseEntity<?> getReplyPage(@PathVariable Long postId,
                                          @PathVariable Long commentId,
                                          @RequestParam Integer size, @RequestParam Integer page) {
        PageResponseDto<ReplyResponseDto> pageResponseDto = replyService.getReplyPage(postId, commentId, size, page);
        return ResponseEntity.ok()
                .body(pageResponseDto);
    }

    @PutMapping("/{replyId}")
    public ResponseEntity<?> updateReply(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @PathVariable Long replyId,
                                         @RequestBody @Valid ReplyRequestDto replyRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<ReplyResponseDto> responseDto =
                replyService.updateReply(postId, commentId, replyId, replyRequestDto, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long postId,
                                         @PathVariable Long commentId,
                                         @PathVariable Long replyId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<Void> responseDto =
                replyService.deleteReply(postId, commentId, replyId, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }

}
