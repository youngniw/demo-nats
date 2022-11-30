package com.example.testnats.controller;

import com.example.testnats.dto.TelemetryDto;
import com.example.testnats.nats.NatsComponent;
import com.example.testnats.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nats")
public class TestController {
    private final NatsComponent natsComponent;
    private final WebSocketHandler webSocketHandler;

    // 응답 없는 publish
    @GetMapping("/publish")
    public void testPublish() {
        natsComponent.publish("msg.example", "Publish by car");
    }

    // 응답 subject 포함하는 publish
    @GetMapping("/publish/reply")
    public void testPublishAndReply() {
        natsComponent.publish("msg.example", "msg.example.reply", "Publish by car Reply to data");
    }

    // 요청
    @GetMapping("/request")
    public ResponseEntity<String> testRequest() throws ExecutionException, InterruptedException {
        String response = natsComponent.request("msg.example2", "Request to example2");

        return ResponseEntity.ok(response);
    }


    // 예시 (서버에서 클라이언트로 웹소켓을 통해 데이터 전달)
    @GetMapping("/test/websocket")
    public void test(TelemetryDto telemetry) {
        ObjectMapper objectMapper = new ObjectMapper();

        Set<WebSocketSession> sessions = webSocketHandler.getSessions();
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(telemetry)));
            } catch (IOException e) {
                throw new RuntimeException("서버에 문제가 발생했습니다.");
            }
        });
    }
}
