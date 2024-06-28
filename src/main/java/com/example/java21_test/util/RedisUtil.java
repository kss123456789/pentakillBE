package com.example.java21_test.util;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j(topic = "redis util")
@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; //2주

    // refresh token redis에 저장
    @Transactional
    public void setRefreshToken(String refreshToken) {
        String tokenvalue = jwtUtil.substringToken(refreshToken);
        Claims info = jwtUtil.getUserInfoFromToken(tokenvalue);
        String uuid = info.get("uuid", String.class);
        log.info(uuid);
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(REFRESH_TOKEN_TIME);
        values.set(uuid, refreshToken, expireDuration);
    }
    // 재발급시 기존 값 삭제
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        String tokenvalue = jwtUtil.substringToken(refreshToken);
        Claims info = jwtUtil.getUserInfoFromToken(tokenvalue);
        String uuid = info.get("uuid", String.class);
        log.info(uuid);
        boolean isDeledted = Boolean.TRUE.equals(redisTemplate.delete(uuid));
        if (!isDeledted) {
            throw new BadCredentialsException("refreshToken 기록이 없습니다.");
        }
    }
}
