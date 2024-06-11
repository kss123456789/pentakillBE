package com.example.java21_test.respository;

import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.PostLike;
import com.example.java21_test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
