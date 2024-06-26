package com.example.java21_test.controller;


import com.example.java21_test.dto.requestDto.LogInRequestDto;
import com.example.java21_test.dto.requestDto.SignUpRequestDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/back/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입 <?> 대신 다른걸 쓰는게 좋을 것 같은데...
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequestDto requestDto, HttpServletResponse jwtResponse) {
        StatusCodeResponseDto<Void> responseDto = userService.signup(requestDto, jwtResponse);
        return ResponseEntity.ok()
                .body(responseDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LogInRequestDto requestDto, HttpServletResponse jwtResponse) {
        StatusCodeResponseDto<Void> responseDto = userService.login(requestDto, jwtResponse);
        return ResponseEntity.ok()
                .body(responseDto);
    }

    // refresh 재발급
    @GetMapping("/refresh")
    public ResponseEntity<?> reissue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        StatusCodeResponseDto<Void> responseDto = userService.reissue(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok()
                .body(responseDto);
    }

}
