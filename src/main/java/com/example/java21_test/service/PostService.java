package com.example.java21_test.service;

import com.example.java21_test.dto.*;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.User;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.respository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "게시판 post CRUD")
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;

    @Transactional
    public StatusCodeResponseDto<PostResponseDto> createPost(PostRequestDto postRequestDto,
                                               User user) {
        String title = postRequestDto.getTitle();
        String content = postRequestDto.getContent();
        Post post = new Post(title, content, user);
        String newContent = s3Service.moveTempFilesToPermanent(content);
        post.update(title, newContent);
        Post savePost = postRepository.save(post);

        PostResponseDto postResponseDto = PostMapper.toDto(savePost, user);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "게시글 생성 완료", postResponseDto);
    }

    public PageResponseDto<PostResponseDto> getPostPage(Integer size, Integer page, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAll(pageable);
        Page<PostResponseDto> postResponseDtoPage = postPage.map(post -> PostMapper.toDto(post, user));

        return new PageResponseDto<>(HttpStatus.OK.value(), "SUCCESS", postResponseDtoPage);
    }

    public StatusCodeResponseDto<PostResponseDto> getPost(Long postId, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("기록을 찾을 수 없습니다."));

        PostResponseDto postResponseDto = PostMapper.toDto(post, user);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 조회 완료", postResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<PostResponseDto> updatePost(Long postId, PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }
        String title = postRequestDto.getTitle();
        String content = postRequestDto.getContent();
        String newContent = s3Service.moveTempFilesToPermanent(content);
        post.update(title, newContent);
        postRepository.flush();
        PostResponseDto postResponseDto = PostMapper.toDto(post, user);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 수정 완료", postResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<Void> deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }
        postRepository.delete(post);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 삭제 완료");
    }

    private User checkUser(UserDetailsImpl userDetails) {
        User user;
        if (userDetails != null) {
            user = userDetails.getUser();
        } else {
            user = null;
        }
        return user;
    }
}