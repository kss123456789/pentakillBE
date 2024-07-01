package com.example.java21_test.service;

import com.example.java21_test.dto.responseDto.SseNoticeResponseDto;
import com.example.java21_test.entity.SseNotice;
import com.example.java21_test.entity.User;
import com.example.java21_test.impl.UserDetailsImpl;
import com.example.java21_test.respository.SseNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j(topic = "sse message 관련")
@Service
@RequiredArgsConstructor
public class SseService {
    private final SseTransactionalService sseTransactionalService;
    private final SseNoticeRepository sseNoticeRepository;

    private final ConcurrentHashMap<SseEmitter, User> emittersMap = new ConcurrentHashMap<>();

    public void addEmitter(SseEmitter emitter, UserDetailsImpl userDetails) {
        emittersMap.put(emitter, userDetails.getUser());
        emitter.onCompletion(() -> emittersMap.remove(emitter));
        emitter.onTimeout(() -> emittersMap.remove(emitter));
    }


    public SseEmitter.SseEventBuilder createEvent(String name, String message) {
        return SseEmitter.event()
                .name(name)
                .data(message)
                .reconnectTime(30000L);
    }

    public void sendConnectEvent(SseEmitter emitter) {
        SseEmitter.SseEventBuilder sseEventBuilder = createEvent("connect", "connected!");
        try {
            emitter.send(sseEventBuilder);
            log.info("connectSuccess");
        } catch (IOException e) {
            log.error("connectFail");
            emitter.complete();
            emittersMap.remove(emitter);
        }
    }

//    @Scheduled(fixedRate = 10000)
//    public void sendTestEvent() {
//        LocalDateTime time = LocalDateTime.now();
//        String team1Name = "T1";
//        String team2Name = "GEN.G";
//
//        String eventName1 = "matchStatusNotice";
//        String eventName2 = "matchResultNotice";
//
//        String outcome = "win";
//        int point = 600;
//
//        List<SseNoticeResponseDto> noticList = new ArrayList<>();
//        noticList.add(new SseNoticeResponseDto(time, eventName1, team1Name, team2Name));
//        noticList.add(new SseNoticeResponseDto(time, eventName2, team1Name, team2Name, point, outcome));
//
//        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
//                .name("matchNotice")
//                .data(noticList)
//                .reconnectTime(3000L);
//        if(emittersMap.keySet().isEmpty()) {
//            log.info("no emitter");
//        }
//
//        for (SseEmitter emitter : emittersMap.keySet()) {
//            try {
//                emitter.send(sseEventBuilder);
//            } catch (IOException e) {
//                emitter.complete();
//                emittersMap.remove(emitter);
//            }
//        }
//    }

    public void sendNotice() {
        for (SseEmitter emitter : emittersMap.keySet()) {

            User user = emittersMap.get(emitter);
            List<SseNotice> sseNoticeList = sseNoticeRepository.findAllByUser(user);
            List<SseNoticeResponseDto> sseNoticeResponseDtoList = sseNoticeList.stream()
                    .map(SseNoticeResponseDto::new)
                    .toList();

            SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                    .name("matchNotice")
                    .data(sseNoticeResponseDtoList)
                    .reconnectTime(3000L);

            try {
                emitter.send(sseEventBuilder);
                sseTransactionalService.deleteNotice(sseNoticeList);

            } catch (IOException e) {
                emitter.complete();
                emittersMap.remove(emitter);
            }
        }
    }
}
