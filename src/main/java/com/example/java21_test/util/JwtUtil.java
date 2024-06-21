package com.example.java21_test.util;

import com.example.java21_test.entity.Point;
import com.example.java21_test.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    // jwt 데이터
    // Header KEY 값 구분을 위한 key값
    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN = "RefreshToken";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // jwt 생성(access token)
    public String createAccessToken(User user, Point point) {
        Date date = new Date();
        // 60분
        final long TOKEN_TIME = 30 * 1000L; // 기존 60 * 60 * 1000L -> 임시 30초
//        final long TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // access token reset 방지용 2주짜리
        return BEARER_PREFIX +
                Jwts.builder()
                        .claim("email", user.getEmail())
                        .claim("username", user.getUsername())
                        .claim("point", point.getPoint())
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String createRefreshToken(User user) {
        Date date = new Date();
        //2주
        final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L;
        return BEARER_PREFIX +
                Jwts.builder()
                        .claim("email", user.getEmail())
                        .claim("uuid", UUID.randomUUID())
                        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // jwt header에 저장
    public void addAccessTokenToHeader(String token, HttpServletResponse res) {
        res.addHeader(AUTHORIZATION_HEADER, token);
    }

    public void addRefreshTokenToHeader(String token, HttpServletResponse res) {
        res.addHeader(REFRESH_TOKEN, token);
    }

    // substring
    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // jwt 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | InvalidClaimException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // jwt에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String tokenValue) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenValue).getBody();
    }

    // HttpServletRequest JWT 가져오기
    public String getAccessTokenFromRequest(HttpServletRequest req) {
        String header = req.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header)) {
            return URLDecoder.decode(header, StandardCharsets.UTF_8);
        }
        return null;
    }

    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        String header = req.getHeader(REFRESH_TOKEN);
        if (StringUtils.hasText(header)) {
            return URLDecoder.decode(header, StandardCharsets.UTF_8);
        }
        return null;
    }
}