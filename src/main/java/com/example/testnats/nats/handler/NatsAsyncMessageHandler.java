package com.example.testnats.nats.handler;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

@Slf4j
@Profile("async")
@Component
public class NatsAsyncMessageHandler {
    private final Connection natsConnection;

    @Value("${nats.stream.subject}")
    private String subject;
    @Value("${nats.stream.queue}")
    private String queue;
    private Dispatcher dispatcher;

    @Autowired
    public NatsAsyncMessageHandler(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    @PostConstruct
    private void init() {
        System.out.println("Dispatcher can manage subject...");

        dispatcher = natsConnection.createDispatcher((message) -> {
            String data = new String(message.getData(), StandardCharsets.UTF_8);
            log.info(String.format("Received Message from %s: %s", message.getSubject(), data));

            /* 데이터 가공해서 처리 */

            if (message.getReplyTo() != null) {
                natsConnection.publish(message.getReplyTo(), "OK".getBytes());      // 요청에 대한 응답 반환
            }
            else ;      // 다시 응답 반환
        });

        if (StringUtils.hasText(queue)) {
            dispatcher.subscribe(subject, queue);
        } else {
            dispatcher.subscribe(subject);
        }

//        // if 지정된 구독에 대한 개별적 콜백
//        dispatcher = natsConnection.createDispatcher((msg) -> {});
//        Subscription sub = dispatcher.subscribe("some.subject", (message) -> {
//            String response = new String(message.getData(), StandardCharsets.UTF_8);
//        });
//        dispatcher.unsubscribe(sub, 100);
    }

    @PreDestroy
    private void destroy() {
        dispatcher.unsubscribe(subject);
    }
}
