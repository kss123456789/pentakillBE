package com.example.java21_test.dto.mapper;

import com.example.java21_test.dto.responseDto.PostResponseDto;
import com.example.java21_test.entity.Comment;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.PostLike;
import com.example.java21_test.entity.User;

import java.time.LocalDateTime;
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
            if (user != null) {
                // 본인 확인
                if (postLike.getUser().getId().equals(user.getId())) {
                    isLike = checkLike;
                }
            }
            // like count
            if (checkLike != null) {
                if (checkLike) {
                    likeCount++;
                } else {
                    dislikeCount++;
                }
            }
        }
        List<Comment> commentList = post.getCommentList();
        Long replyCount = commentList.stream()
                .map(Comment::getReplyList)
                .mapToLong(List::size)
                .sum();
        Long commentCount = commentList.size() + replyCount;
        Long views = post.getViews();
        LocalDateTime createdAt = post.getCreatedAt();
        LocalDateTime modifiedAt = post.getModifiedAt();
        String nickname = post.getUser().getUsername();
        String email = post.getUser().getEmail();

        return new PostResponseDto(id, title, content,
                isLike, likeCount, dislikeCount, commentCount, views,
                createdAt, modifiedAt, nickname, email);
    }
}
