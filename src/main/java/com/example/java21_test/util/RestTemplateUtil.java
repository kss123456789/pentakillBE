package com.example.java21_test.util;

import com.example.java21_test.dto.requestDto.ProbabilityRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.CredentialException;
import java.net.URI;

@Slf4j
@Component
public class RestTemplateUtil {
    @Value("${x-api-key}")
    private String AUTH_KEY;
    public ResponseEntity<String> getDataFromAPI(URI targetUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-api-key", AUTH_KEY); //Authorization 설정
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders); //엔티티로 만들기

        return restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);
    }

    public ResponseEntity<String> getProbabilityFromDS(URI targetUrl, ProbabilityRequestDto probabilityRequestDto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<ProbabilityRequestDto> httpEntity = new HttpEntity<>(probabilityRequestDto, httpHeaders); //엔티티로 만들기
        return restTemplate.exchange(targetUrl, HttpMethod.POST, httpEntity, String.class);
    }

    public ResponseEntity<String> getUserDataFromGoogleApi(URI targetUrl, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        log.info(httpHeaders.toString());
        HttpEntity<ProbabilityRequestDto> httpEntity = new HttpEntity<>(httpHeaders); //엔티티로 만들기
        try {
            return restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException("GoogleAccessToken에 문제가 있습니다.");
        }
    }
}
