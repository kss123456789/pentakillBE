package com.example.java21_test.controller;

import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/back/sse")
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping(value = "/emitter", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        //SseEmitter는 서버에서 클라이언트로 이벤트를 전달할 수 있습니다.
        SseEmitter emitter = new SseEmitter(300_000L);
        sseService.addEmitter(emitter, userDetails);
        sseService.sendConnectEvent(emitter);
        return emitter;
    }
}
