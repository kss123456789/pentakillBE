package com.example.java21_test.service;

import com.example.java21_test.dto.mapper.PostMapper;
import com.example.java21_test.dto.requestDto.PostRequestDto;
import com.example.java21_test.dto.responseDto.PageResponseDto;
import com.example.java21_test.dto.responseDto.PostResponseDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Post;
import com.example.java21_test.entity.User;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.respository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j(topic = "게시판 post CRUD")
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;

    @Transactional
    public StatusCodeResponseDto<PostResponseDto> createPost(PostRequestDto postRequestDto,
                                                             User user) {
        String title = postRequestDto.getTitle();
        String content = postRequestDto.getContent();
        Post post = new Post(title, content, user);
        Post savePost = postRepository.save(post);
        // post 작성시에 에러 발생하면 하단의 이미지 이동도 일어나지 않음
        String newContent = s3Service.moveTempFilesToPermanent(content);
        post.update(title, newContent);

        PostResponseDto postResponseDto = PostMapper.toDto(savePost, user);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "게시글 생성 완료", postResponseDto);
    }

    public PageResponseDto<PostResponseDto> getPostPage(int size, int page, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAll(pageable);
        Page<PostResponseDto> postResponseDtoPage = postPage.map(post -> PostMapper.toDto(post, user));

        return new PageResponseDto<>(HttpStatus.OK.value(), "SUCCESS", postResponseDtoPage);
    }

    @Transactional
    public StatusCodeResponseDto<PostResponseDto> getPost(Long postId, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        // 조회수 증가 로직
        post.raiseViews();
        PostResponseDto postResponseDto = PostMapper.toDto(post, user);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 조회 완료", postResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<PostResponseDto> updatePost(Long postId, PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AuthorizationServiceException("작성자가 아닙니다.");
        }

        String title = postRequestDto.getTitle();
        String content = postRequestDto.getContent();
        String newContent = s3Service.moveTempFilesToPermanent(content);
        String oldContent = post.getContent();
        // 수정전 이미지링크들에서 중복(삭제안해도 되는 이미지)을 제거하고 삭제된 이미지 링크를 포함하는 content
        String contentIncludeRemovedImg = removeDuplicateImgTags(oldContent, newContent);
        s3Service.deletePermanentFile(contentIncludeRemovedImg);

        post.update(title, newContent);
        PostResponseDto postResponseDto = PostMapper.toDto(post, user);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 수정 완료", postResponseDto);
    }

    @Transactional
    public StatusCodeResponseDto<Void> deletePost(Long postId, UserDetailsImpl userDetails) {
        User user = checkUser(userDetails);
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AuthorizationServiceException("작성자가 아닙니다.");
        }
        s3Service.deletePermanentFile(post.getContent());
        postRepository.delete(post);
        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "게시글 삭제 완료");
    }

    private User checkUser(UserDetailsImpl userDetails) {
        User user;
        if (userDetails != null) {
            user = userDetails.getUser();
        } else {
            user = null;
        }
        return user;
    }

    public static String removeDuplicateImgTags(String oldContent, String newContent) {

        Document oldDoc = Jsoup.parse(oldContent);
        Elements oldImgs = oldDoc.select("img");
        Document newDoc = Jsoup.parse(newContent);
        Elements newImgs = newDoc.select("img");

        // 두 번째 HTML의 이미지 src 값을 집합에 저장
        Set<String> newImgSrcSet = new HashSet<>();
        for (Element img : newImgs) {
            newImgSrcSet.add(img.attr("src"));
        }
        // 첫 번째 HTML에서 중복된 img 태그 제거
        for (Element img : oldImgs) {
            if (newImgSrcSet.contains(img.attr("src"))) {
                img.remove();
            }
        }
        // 첫 번째 HTML의 업데이트된 내용을 반환
        return oldDoc.body().html();
    }

}
