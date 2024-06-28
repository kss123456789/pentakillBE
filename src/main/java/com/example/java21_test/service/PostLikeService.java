package com.example.java21_test.service;

import com.example.java21_test.dto.responseDto.PostLikeResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.PostLike;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.PostLikeRepository;
import com.example.java21_test.respository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "postLike CRUD")
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public StatusCodeResponseDto<PostLikeResponseDto> updatePostLike(Long postId, Boolean isLike, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);
        if (postLike == null) {
            postLike = new PostLike(user, post, isLike);
            postLikeRepository.save(postLike);
        }
        else {
            postLike.update(isLike);
        }

        PostLikeResponseDto postLikeResponseDto = new PostLikeResponseDto(postLike);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "좋아요 수정 완료", postLikeResponseDto);
    }
}
