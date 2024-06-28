package com.example.java21_test.controller;

import com.example.java21_test.dto.requestDto.PostRequestDto;
import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.responseDto.PostResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.PostService;
import com.example.java21_test.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/back/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

    @PostMapping("/images")
    public ResponseEntity<?> uploadImage(List<MultipartFile> files) throws IOException {
        StatusCodeResponseDto<List<String>> responseDto = s3Service.upload(files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping()
    public ResponseEntity<?> createPost(@RequestBody @Valid PostRequestDto postRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<PostResponseDto> responseDto = postService.createPost(postRequestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping()
    public ResponseEntity<?> getPostPage(@RequestParam Integer size, @RequestParam Integer page,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PageResponseDto<PostResponseDto> pageResponseDto = postService.getPostPage(size, page, userDetails);
        return ResponseEntity.ok()
                .body(pageResponseDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<PostResponseDto> responseDto = postService.getPost(postId, userDetails);
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody @Valid PostRequestDto postRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StatusCodeResponseDto<PostResponseDto> responseDto = postService.updatePost(postId, postRequestDto, userDetails);
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        StatusCodeResponseDto<Void> responseDto = postService.deletePost(postId, userDetails);
        return ResponseEntity.ok()
                .body(responseDto);
    }
}
