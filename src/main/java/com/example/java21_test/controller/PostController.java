package com.example.java21_test.controller;

import com.example.java21_test.dto.PageResponseDto;
import com.example.java21_test.dto.PostRequestDto;
import com.example.java21_test.dto.PostResponseDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PostService;
import com.example.java21_test.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;
    @PostMapping()
    public StatusCodeResponseDto<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(postRequestDto, userDetails.getUser());
    }

    @GetMapping()
    public PageResponseDto<PostResponseDto> getPostPage(Integer size, Integer page,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPostPage(size, page, userDetails);
    }

    @GetMapping("/{postId}")
    public StatusCodeResponseDto<PostResponseDto> getPost(@PathVariable Long postId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId, userDetails);
    }

    @PutMapping("/{postId}")
    public StatusCodeResponseDto<PostResponseDto> updatePost(@PathVariable Long postId,
                                                             @RequestBody PostRequestDto postRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(postId, postRequestDto, userDetails);
    }

    @DeleteMapping("/{postId}")
    public StatusCodeResponseDto<Void> deletePost(@PathVariable Long postId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails);
    }

    @PostMapping("/images")
    public StatusCodeResponseDto<List<String>> uploadImage(List<MultipartFile> files) throws IOException {
        return s3Service.upload(files);
    }

    @GetMapping("/deleteTemp")
    public StatusCodeResponseDto<?> deleteTemp() {
        return s3Service.deleteOldTempFiles();
    }
}