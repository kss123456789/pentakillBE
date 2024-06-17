package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.CommentMapper;
import com.example.java21_test.dto.requestDto.CommentRequestDto;
import com.example.java21_test.dto.responseDto.CommentResponseDto;
import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Comment;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.CommentRepository;
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

@Slf4j(topic = "comment CRUD")
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public StatusCodeResponseDto<CommentResponseDto> createComment(Long postId, CommentRequestDto commentRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        String content = commentRequestDto.getContent();
        Comment comment = new Comment(content, user, post);
        commentRepository.save(comment);
        commentRepository.flush();

        CommentResponseDto commentResponseDto = CommentMapper.toDto(comment);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "댓글 생성 완료", commentResponseDto);
    }


    public PageResponseDto<CommentResponseDto> getCommentPage(Long postId, Integer size, Integer page) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findAllByPost(post, pageable);
        Page<CommentResponseDto> commentResponseDtoPage = commentPage.map(CommentMapper::toDto);
        return new PageResponseDto<>(HttpStatus.OK.value(), "댓글 페이지 조회", commentResponseDtoPage);
    }

    @Transactional
    public StatusCodeResponseDto<CommentResponseDto> updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto,
                                                                   User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }
        String content = commentRequestDto.getContent();
        comment.update(content);
        commentRepository.flush();

        CommentResponseDto commentResponseDto = CommentMapper.toDto(comment);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "댓글 수정 완료", commentResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<Void> deleteComment(Long postId, Long commentId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }
        commentRepository.delete(comment);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "댓글 삭제 완료");
    }
}
