package com.example.java21_test.respository;

import com.example.java21_test.entity.Comment;
import com.example.java21_test.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findAllByComment(Comment comment, Pageable pageable);

}
