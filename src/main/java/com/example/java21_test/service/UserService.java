package com.example.java21_test.service;

import com.example.java21_test.dto.LogInRequestDto;
import com.example.java21_test.dto.SignUpRequestDto;
import com.example.java21_test.dto.StatusCodeResponseDto;
import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import com.example.java21_test.respository.UserRepository;
import com.example.java21_test.util.JwtUtil;
import com.example.java21_test.util.PointUtil;
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
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PointUtil pointUtil;

//    @Value("${adimin.token}") // Base64 Encode 한 SecretKey
//    private String ADMIN_TOKEN;
    // Access_Token,  Refresh_Token

    //회원가입
    @Transactional
    public StatusCodeResponseDto<Void> signup(SignUpRequestDto requestDto, HttpServletResponse jwtResponse) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        //회원 중복 확인
        Optional<User> checkUsername = userRepository.findByEmail(email);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
        // user 생성시 point도 같이 생성되도록 함... user 삭제시 point 연관관계 좀더 고민....
        // 사용자 등록
        User user = new User(username, email, password);
        userRepository.save(user);
        user = userRepository.findByEmail(email).orElseThrow(() ->
            new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        // 유저별 point db 생성
        Point point = new Point(user);
        pointRepository.save(point);
        point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        // 가입 포인트
        PointLog welcomePointLog = new PointLog(1000, "signUp", point); // enum사용으로 바꾸기...
        pointUtil.getPoint(welcomePointLog);

        pointLogRepository.save(welcomePointLog);

        // Jwt 토큰 생성, response에 넣기
        String token = jwtUtil.createToken(user, point);
        // Jwt Header
        jwtUtil.addJwtToHeader(token, jwtResponse);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "회원가입 성공");
    }

    //    //로그인    security filter에서 하는 방법도 있는데 이게 더 맞는 방법.
    public StatusCodeResponseDto<Void> login(LogInRequestDto requestDto, HttpServletResponse jwtResponse) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> //Optional<T>에 orElseThrow 메서드는 결과값이 T로 나온다 (User)
                new IllegalArgumentException("회원을 찾을 수 없습니다."));
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트가 존재하지 않습니다."));

        // Jwt 토큰 생성, response에 넣기
        String token = jwtUtil.createToken(user, point);
//        // Jwt Header
        jwtUtil.addJwtToHeader(token, jwtResponse);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "로그인 성공");
    }
}
