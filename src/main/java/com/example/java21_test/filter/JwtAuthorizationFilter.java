package com.example.java21_test.filter;

import com.example.java21_test.impl.UserDetailsServiceImpl;
import com.example.java21_test.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")  //인증 및 인가
public class JwtAuthorizationFilter extends OncePerRequestFilter {  //무조건 필터를 거친다...

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = jwtUtil.getAccessTokenFromRequest(req);
        log.info(accessToken);

        if (StringUtils.hasText(accessToken)) {
            // JWT 토큰 substring
            String tokenValue = jwtUtil.substringToken(accessToken);
            log.info(tokenValue);
            // 토큰 검증
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
            }
            // 토큰에서 사용자 정보 가져오기
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.get("email", String.class));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}