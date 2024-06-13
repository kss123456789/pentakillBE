package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.requestDto.ReplyRequestDto;
import com.example.java21_test.dto.responseDto.ReplyResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping()
    public StatusCodeResponseDto<ReplyResponseDto> createReply(@PathVariable Long postId,
                                                                 @PathVariable Long commentId,
                                                                 @RequestBody ReplyRequestDto replyRequestDto,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return replyService.createReply(postId, commentId, replyRequestDto, userDetails.getUser());
    }

    @GetMapping()
    public PageResponseDto<ReplyResponseDto> getReplyPage(@PathVariable Long postId,
                                                            @PathVariable Long commentId,
                                                            Integer size, Integer page) {
        return replyService.getReplyPage(postId, commentId, size, page);
    }

    @PutMapping("/{replyId}")
    public StatusCodeResponseDto<ReplyResponseDto> updateReply(@PathVariable Long postId,
                                                                 @PathVariable Long commentId,
                                                                 @PathVariable Long replyId,
                                                                 @RequestBody ReplyRequestDto replyRequestDto,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return replyService.updateReply(postId, commentId, replyId, replyRequestDto, userDetails.getUser());
    }

    @DeleteMapping("/{replyId}")
    public StatusCodeResponseDto<Void> deleteReply(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @PathVariable Long replyId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return replyService.deleteReply(postId, commentId, replyId, userDetails.getUser());
    }

}
