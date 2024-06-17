package com.example.java21_test.util;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

        log.info(redisTemplate.getExpire(uuid).toString());


        Boolean isDeledted = redisTemplate.delete(uuid);
        if (!isDeledted) {
            throw new NullPointerException("refreshToken 기록이 없습니다.");
        }
    }
    // 전부 로그아웃 처리 하고 싶을때 사용 userid와 uuid를 합쳐서 key를 만들었을때 userid가 일치하는걸 싹다 삭제
//    @Transactional
//    public void deleteAllRefreshToken(String email) {
//        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        Set<String> keys = redisTemplate.keys("^Bearer\\s.+");
//        for (String key : keys) {
//            String refreshToken = key.substring(7);
//            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
//            String usernameFromRefreshToken = info.get("email", String.class);
//            if (usernameFromRefreshToken.equals(email)) {
//                redisTemplate.delete(key);
//            }
//        }
//    }


//    public Boolean limitAccess(String nickname) {
//        ValueOperations<String, String> values = redisTemplate.opsForValue();
//        int count = 0;
//        // Redis에서 key를 찾기 위해 모든 키를 순회합니다.
//        Set<String> keys = redisTemplate.keys("^Bearer\\s.+");
//        for (String key : keys) {
//            String refreshToken = key.substring(7);
//            log.info("expired test1");
//            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
//            log.info("expired test2");
//            String usernameFromRefreshToken = info.get("nickname", String.class);
//            if (usernameFromRefreshToken.equals(nickname)) {
//                count++;
//            }
//        }
//        return count > 4 ? true : false;
//    }
}
