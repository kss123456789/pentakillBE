package com.example.java21_test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Long views = 0L; //중복 조회를 통한 조작을 막으려면 조회한 사람의 리스트가 있어야 한다 -> 별도로 views의 entity, table이 필요하다.
    //아니면 redis를 이용해 조회시 사용자의 ip를 저장하면서 유지기간을 하루로 정하여 redis에 동일 ip 또는 mac 주소로 접속기록이 있을시 조회수가 올라가지 않도록 하여 작성할까?

    // 연관관계한 table fetch type 대부분 lazy로 수정해야 겠다...
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostLike> postLikeList = new ArrayList<>();

    // 댓글 요청 api가 따로라면 fetchtype을 lazy로 해도 되겟지... 대댓글(답글)갯수를 표시해주려면 댓글에서 대댓글을 eager로 하거나 대댓글 수 column을 따로 만들어야 겟지...
    // 나중에 물어보자... 댓글 수 대댓글 수 필요한지...
    @OneToMany(mappedBy = "post")
//    @OrderBy("createdAt desc")
    private List<Comment> commentList = new ArrayList<>();


    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}