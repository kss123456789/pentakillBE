package com.example.java21_test.service;

import com.example.java21_test.dto.requestDto.LogInRequestDto;
import com.example.java21_test.dto.requestDto.SignUpRequestDto;
import com.example.java21_test.dto.responseDto.StatusCodeResponseDto;
import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.PointLog;
import com.example.java21_test.entity.User;
import com.example.java21_test.respository.PointLogRepository;
import com.example.java21_test.respository.PointRepository;
import com.example.java21_test.respository.UserRepository;
import com.example.java21_test.util.JwtUtil;
import com.example.java21_test.util.PointUtil;
import com.example.java21_test.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PointUtil pointUtil;
    private final RedisUtil redisUtil;

    //회원가입
    @Transactional
    public StatusCodeResponseDto<Void> signup(SignUpRequestDto requestDto, HttpServletResponse httpServletResponse) {
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
                // 서버 문제
                 new RuntimeException("회원을 찾을 수 없습니다.")
        );
        // 유저별 point db 생성
        Point point = new Point(user);
        pointRepository.save(point);
        point = pointRepository.findByUser(user).orElseThrow(() ->
                new RuntimeException("회원을 찾을 수 없습니다.")
        );
        // 가입 포인트
        PointLog welcomePointLog = new PointLog(1000, "signUp", point); // enum사용으로 바꾸기...
        pointUtil.getPoint(welcomePointLog);
        pointLogRepository.save(welcomePointLog);
        // access token, refresh 토큰 발급
        addTokensToHeader(user, point, httpServletResponse);

        return new StatusCodeResponseDto<>(HttpStatus.CREATED.value(), "회원가입 성공");
    }

    //    //로그인    security filter에서 하는 방법도 있는데 이게 더 맞는 방법.
    public StatusCodeResponseDto<Void> login(LogInRequestDto requestDto, HttpServletResponse httpServletResponse) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> //Optional<T>에 orElseThrow 메서드는 결과값이 T로 나온다 (User)
                new BadCredentialsException("이메일과 비밀번호를 확인해 주세요."));
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("이메일과 비밀번호를 확인해 주세요.");
        }
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                // 유저는 있는데 point가 없는 경우 -> 서버 문제
                new RuntimeException("포인트가 존재하지 않습니다."));
        // access token, refresh 토큰 발급
        addTokensToHeader(user, point, httpServletResponse);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "로그인 성공");
    }

    public StatusCodeResponseDto<Void> reissue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // refresh 토큰 유효성 검사
        String refreshToken = jwtUtil.getRefreshTokenFromRequest(httpServletRequest);
        String tokenValue = jwtUtil.substringToken(refreshToken);
        boolean isValidate = jwtUtil.validateToken(tokenValue);
        // 이전 토큰 삭제
        if (isValidate) {
            redisUtil.deleteRefreshToken(refreshToken); // refresh 토큰 없다 오류 가능
        } else {
            log.info("refresh validate 불가");
            throw new BadCredentialsException("Refresh token is not validated");
        }
        // refresh token으로부터 email 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        String email = info.get("email", String.class);

        // 사용자 확인, point 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> //Optional<T>에 orElseThrow 메서드는 결과값이 T로 나온다 (User)
                new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Point point = pointRepository.findByUser(user).orElseThrow(() ->
                new IllegalArgumentException("포인트가 존재하지 않습니다."));
        // access token, refresh 토큰 발급
        addTokensToHeader(user, point, httpServletResponse);

        return new StatusCodeResponseDto<>(HttpStatus.OK.value(), "재발급 성공");
    }

    public void addTokensToHeader(User user, Point point, HttpServletResponse httpServletResponse) {
        // access 토큰 생성, header에 넣기
        String accessToken = jwtUtil.createAccessToken(user, point);
        log.info("accessToken : " + accessToken);
        jwtUtil.addAccessTokenToHeader(accessToken, httpServletResponse);
        // refresh 토큰 생성, header에 넣기, redis에 저장
        String refreshToken = jwtUtil.createRefreshToken(user);
        log.info("refreshToken : " + refreshToken);
        jwtUtil.addRefreshTokenToHeader(refreshToken, httpServletResponse);
        redisUtil.setRefreshToken(refreshToken);
    }
}
