package com.example.java21_test.controller;

import com.example.java21_test.dto.responseDto.PostLikeResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/back/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    @PutMapping()
    public ResponseEntity<?> updatePostLike(@PathVariable Long postId,
                                            @PathVariable Boolean isLike,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<PostLikeResponseDto> responseDto =
                postLikeService.updatePostLike(postId, isLike, userDetails.getUser());
        return ResponseEntity.ok()
                .body(responseDto);
    }
}
