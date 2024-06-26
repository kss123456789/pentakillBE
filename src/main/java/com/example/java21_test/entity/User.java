package com.example.java21_test.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

//    point를 따로 entity를 만들고 로그(증가, 감소 기록)도 같이 저장하는것이 좋을까나...?
//    다만 이 경우 단순 point값만이 필요한 경우에 불필요한 값 가져오는데 손실이 생길것으로 생각
//    PointLog와 같은 entity를 하나 만들어 userId만 받아와서 사용하는게 좋을것으로 추정
    @OneToOne(mappedBy = "user")
    private Point point;

    @OneToMany(mappedBy = "user")
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Reply> replyList = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
