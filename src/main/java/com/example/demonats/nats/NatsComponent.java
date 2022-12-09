package com.example.demonats.nats;

import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Slf4j
@Component
public class NatsComponent {
    private final Connection natsConnection;

    @Autowired
    public NatsComponent(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    // subject로 메시지(message)를 보냄
    public void publish(String subject, String message) {
        natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
    }

    // subject로 메시지(message)를 보냄 (+ 응답 subject 포함)
    public void publish(String subject, String replyTo, String message) {
        natsConnection.publish(subject, replyTo, message.getBytes(StandardCharsets.UTF_8));
    }

    /* 메시지 객체를 넘겼을 때의 publish */

    // 요청
    public CompletableFuture<Message> request(String subject, String message) throws ExecutionException, InterruptedException {
        CompletableFuture<Message> incoming = natsConnection.request(subject, message.getBytes(StandardCharsets.UTF_8));
        CompletableFuture<Message> nextFuture = incoming.thenApply(msg -> {
            log.info("Reply message: {}", new String(msg.getData(), StandardCharsets.UTF_8));

            /* 회신받은 데이터를 이용한 작업 처리 */

            return msg;
        });

        return nextFuture;
    }
}
