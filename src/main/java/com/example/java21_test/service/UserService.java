package com.example.java21_test.service;

import com.example.java21_test.dto.LogInRequestDto;
import com.example.java21_test.dto.SignUpRequestDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.UserRepository;
import com.example.java21_test.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

//    @Value("${adimin.token}") // Base64 Encode 한 SecretKey
//    private String ADMIN_TOKEN;
    // Access_Token,  Refresh_Token

    //회원가입
    @Transactional
    public StatusCodeResponseDto signup(SignUpRequestDto requestDto, HttpServletResponse jwtResponse) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        //회원 중복 확인
        Optional<User> checkUsername = userRepository.findByEmail(email);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        User user = new User(username, email, password, 0);
        userRepository.save(user);

        // Jwt 토큰 생성, response에 넣기
        String token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUsername());
        // Jwt Header
        jwtUtil.addJwtToHeader(token, jwtResponse);

        return new StatusCodeResponseDto(HttpStatus.CREATED.value(), "회원가입 성공", null);
    }

    //    //로그인    security filter에서 하는 방법도 있는데 이게 더 맞는 방법.
    public StatusCodeResponseDto login(LogInRequestDto requestDto, HttpServletResponse jwtResponse) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> //Optional<T>에 orElseThrow 메서드는 결과값이 T로 나온다 (User)
                new IllegalArgumentException("회원을 찾을 수 없습니다."));
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        // Jwt 토큰 생성, response에 넣기
        String token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUsername());
//        // Jwt Header
        jwtUtil.addJwtToHeader(token, jwtResponse);

        return new StatusCodeResponseDto(HttpStatus.OK.value(), "로그인 성공", null);
    }
}
