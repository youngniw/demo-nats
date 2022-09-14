package com.example.testnats.nats;

import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public String request(String subject, String message) throws ExecutionException, InterruptedException, TimeoutException {
        Future<Message> incoming = natsConnection.request(subject, message.getBytes(StandardCharsets.UTF_8));
        Message msg = incoming.get(500, TimeUnit.MILLISECONDS);

        return new String(msg.getData(), StandardCharsets.UTF_8);
    }
}
