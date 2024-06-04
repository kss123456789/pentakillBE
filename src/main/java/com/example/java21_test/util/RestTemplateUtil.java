package com.example.java21_test.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

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

}
