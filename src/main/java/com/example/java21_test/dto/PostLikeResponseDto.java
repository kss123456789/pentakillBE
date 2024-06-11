package com.example.java21_test.dto;

import com.example.java21_test.entity.PostLike;
import lombok.Getter;

@Getter
public class PostLikeResponseDto {
    private Boolean isLike;
    private String nickname;

    public PostLikeResponseDto(PostLike postLike) {
        this.isLike = postLike.getIsLike();
        this.nickname = postLike.getUser().getUsername();
    }
}
