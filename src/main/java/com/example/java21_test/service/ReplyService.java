package com.example.java21_test.service;

import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.requestDto.ReplyRequestDto;
import com.example.java21_test.dto.responseDto.ReplyResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Comment;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.Reply;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.CommentRepository;
import com.example.java21_test.respository.PostRepository;
import com.example.java21_test.respository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "reply CRUD")
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public StatusCodeResponseDto<ReplyResponseDto> createReply(Long postId, Long commentId,
                                                                 ReplyRequestDto replyRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        String content = replyRequestDto.getContent();
        Reply reply = new Reply(content, user, comment);
        replyRepository.save(reply);
        replyRepository.flush();

        ReplyResponseDto replyResponseDto = new ReplyResponseDto(reply);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "답글 생성 완료", replyResponseDto);
    }

    public PageResponseDto<ReplyResponseDto> getReplyPage(Long postId, Long commentId, Integer size, Integer page) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Reply> replyPage = replyRepository.findAll(pageable);
        Page<ReplyResponseDto> replyResponseDtoPage = replyPage.map(ReplyResponseDto::new);

        return new PageResponseDto<>(HttpStatus.OK.value(), "답글 페이지 조회", replyResponseDtoPage);
    }

    @Transactional
    public StatusCodeResponseDto<ReplyResponseDto> updateReply(Long postId, Long commentId, Long replyId,
                                                               ReplyRequestDto replyRequestDto, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() ->
                new IllegalArgumentException("답글을 찾을 수 없습니다."));

        if (!reply.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }

        String content = replyRequestDto.getContent();
        reply.update(content);
        replyRepository.flush();
        ReplyResponseDto replyResponseDto = new ReplyResponseDto(reply);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "답글 수정 완료", replyResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<Void> deleteReply(Long postId, Long commentId, Long replyId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() ->
                new IllegalArgumentException("답글을 찾을 수 없습니다."));

        if (!reply.getUser().getId().equals(user.getId())) {
            return new StatusCodeResponseDto<>(HttpStatus.FORBIDDEN.value(), "작성자가 아닙니다.");
        }

        replyRepository.delete(reply);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "답글 삭제 완료");

    }
}
