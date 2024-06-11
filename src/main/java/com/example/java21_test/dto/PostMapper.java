package com.example.java21_test.dto;

import com.example.java21_test.entity.Comment;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.PostLike;
import com.example.java21_test.entity.User;

import java.time.Instant;
import java.util.List;

public class PostMapper {
    public static PostResponseDto toDto(Post post, User user) {
        Long id = post.getId();
        String title = post.getTitle();
        String content = post.getContent();
        List<PostLike> postLikeList = post.getPostLikeList();
        Boolean isLike = null;
        Long likeCount = 0L;
        Long dislikeCount = 0L;
        for (PostLike postLike : postLikeList) {
            Boolean checkLike = postLike.getIsLike();
            if (postLike.getUser().equals(user)) {
                isLike = checkLike;
            }
            if (checkLike != null) {
                if (checkLike.booleanValue()) {
                    likeCount++;
                } else if (!checkLike.booleanValue()) {
                    dislikeCount++;
                }
            }
        }
        List<Comment> commentList = post.getCommentList();
        Long commentCount = (long) commentList.size();
        Long views = post.getViews();
        Instant createAt = post.getCreatedAt();
        Instant modifiedAt = post.getModifiedAt();
        String nickname = post.getUser().getUsername();

        PostResponseDto postResponseDto = new PostResponseDto(id, title, content,
                isLike, likeCount, dislikeCount, commentCount, views,
                createAt, modifiedAt, nickname);
        return postResponseDto;
    }
}
