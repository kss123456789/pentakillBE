package com.example.java21_test.controller;


import com.example.java21_test.dto.requestDto.LogInRequestDto;
import com.example.java21_test.dto.requestDto.SignUpRequestDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입 <?> 대신 다른걸 쓰는게 좋을 것 같은데...
    @PostMapping("/signup")
    public StatusCodeResponseDto<Void> signup(@RequestBody @Valid SignUpRequestDto requestDto, HttpServletResponse jwtResponse) {
        return userService.signup(requestDto, jwtResponse);
    }

    // 로그인
    @PostMapping("/login")
    public StatusCodeResponseDto<Void> login(@RequestBody LogInRequestDto requestDto, HttpServletResponse jwtResponse) {
        return userService.login(requestDto, jwtResponse);
    }

}
